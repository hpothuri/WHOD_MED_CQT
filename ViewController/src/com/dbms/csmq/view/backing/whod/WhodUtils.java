package com.dbms.csmq.view.backing.whod;


import com.dbms.csmq.CSMQBean;
import com.dbms.csmq.UserBean;
import com.dbms.csmq.model.infNotes.WHODInfoNotesVORowImpl;
import com.dbms.csmq.view.util.ADFUtils;
import com.dbms.util.Utils;
import com.dbms.util.dml.DMLUtils;
import com.dbms.util.logged.CallableStatement;

import java.sql.SQLException;
import java.sql.Types;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.jbo.ViewObject;
import oracle.jbo.server.DBTransaction;

import oracle.jdbc.OracleTypes;


public class WhodUtils {
    public WhodUtils() {
        super();
    }
    private static final String RETURN_SUCCESS = "0";

    public static boolean delete(String dictContentID, String predictGroupName) {

        /*
        FUNCTION delete_content_data

               (pDictContentID   IN  NUMBER,

                pOutDeleteAction OUT VARCHAR2)

             RETURN VARCHAR2;

        - This API may ONLY be Used to Delete a Pre-Existing PREDICT  ***

        --    ***       Content INSERT/UPDATE Record within the DRAFT Activation    ***

        --    ***       Group that is No Longer Desired.                            ***

        --    ***       If the Record is an INSERT of a New "Company" Term that     ***

        --    ***       has Never been "ACTIVATED" within the TMS Repository, this  ***

        --    ***       API will DELETE the Term from PREDICT Record as well as     ***

        --    ***       any Corresponding TMS Components in PREDICT Such as         ***

        --    ***       Informative Notes and Relationships.  In Addition, the      ***

        --    ***       Workflow State and Designee Records will Also be DELETED.   ***

        --    ***       This API will Essentially Eradicate the Term from TMS.      ***

        --    ***       If the Record is an UPDATE of a "Company" Term then this    ***

        --    ***       API will Delete Only the CONTENT Term witin the DRAFT       ***

        --    ***       Activation Group.  If there are No Other Related Components ***

        --    ***       Associated with the Term in PREDICT Such as Informative     ***

        --    ***       Notes or Relationships then the Workflow State will be      ***

        --    ***       Updated back to "ACTIVATED".  If Other TMS Components Exist ***

        --    ***       for the Specified Term in PREDICT within the DRAFT          ***

        --    ***       Activation Group then the Workflow State will Remain as is  ***

        --    ***       and No Update will be Done.                                 ***

        --    ***       TMS "Company" Filter Dictionary Terms may NEVER be DELETED  ***

        --    ***       See Also API RETIRE_TERM in the Delete API Section.

         */
        boolean retVal = false;

        CSMQBean.logger.info("DELETING: " + dictContentID + " FROM : " + predictGroupName);
        String sql = "{? = call cqt_whod_ui_tms_utils.delete_content_data(?,?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        FacesMessage msg;
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "cqt_whod_ui_tms_utils.delete_content_data");

        try {
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.setInt(2, new Integer(dictContentID));
            //cstmt.setString(2, dictContentID);
            cstmt.registerOutParameter(3, Types.VARCHAR);

            cstmt.executeUpdate();
            predictGroupName = cstmt.getString(3) != null ? cstmt.getString(3) : "";
            System.out.println("------" + predictGroupName);
            cstmt.close();
            //msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Term Deleted Successfully", null);
            retVal = true;
        } catch (SQLException e) {
            CSMQBean.logger.error(e.getMessage());
            e.printStackTrace();
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to Delete Query", e.getMessage());
            retVal = true;
        }
        //FacesContext.getCurrentInstance().addMessage(null, msg);
        return retVal;
    }


    public static boolean activate(String dictContentID, String mQCode, String userRole, String userStatus) {

        /*
      NMAT_UI_PKG.reactivate_retire_nmq (
                    pContent_code     IN tms_dict_contents.dict_content_code%TYPE,
                    pActivation_Group IN tms.tms_predict_groups.name%TYPE,
                    pStatus           IN tms_dict_contents.status%TYPE,
                    pUserRole         IN VARCHAR2,
                    pUserName         IN nmq_track_state.created_user%TYPE)

         */
        CSMQBean.logger.info("ACTIVATING: " + mQCode);
        CSMQBean.logger.info(CSMQBean.getProperty("DEFAULT_DRAFT_RELEASE_GROUP"));
        String sql = "{call NMAT_UI_pkg.reactivate_retire_nmq(?,?,?,?,?,?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        FacesMessage msg;
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "NMAT_UI_pkg.reactivate_retire_nmq");
        boolean retVal = false;
        try {
            cstmt.setString("pContent_ID", dictContentID);
            cstmt.setString("pContent_code", mQCode);
            cstmt.setString("pActivation_Group", CSMQBean.getProperty("DEFAULT_DRAFT_RELEASE_GROUP"));
            cstmt.setString("pStatus", CSMQBean.ACTIVE_ACTIVITY_STATUS);
            cstmt.setString("pUserRole", userRole);
            cstmt.setString("pUserName", userStatus);
            cstmt.executeUpdate();
            cstmt.close();
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "MedDRA Query Ractivated Successfully", null);
            retVal = true;
        } catch (SQLException e) {
            if (e.getMessage().indexOf(CSMQBean.INVALID_PROMOTION_ERROR) > -1) {
                String messageText = CSMQBean.getProperty("ACTIVATE_DURING_IMPACT_ANALYSYS");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
            } else {
                CSMQBean.logger.error(e.getMessage());
                e.printStackTrace();
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to Activate MedDRA Query", e.getMessage());
            }
            retVal = false;
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        return retVal;
    }

    public static boolean retire(String dictContentID, String mQCode, String userRole, String userStatus) {

        /*
       NMAT_UI_PKG.reactivate_retire_nmq (
                    pContent_code     IN tms_dict_contents.dict_content_code%TYPE,
                    pActivation_Group IN tms.tms_predict_groups.name%TYPE,
                    pStatus           IN tms_dict_contents.status%TYPE,
                    pUserRole         IN VARCHAR2,
                    pUserName         IN nmq_track_state.created_user%TYPE)

         */
        CSMQBean.logger.info(" *** RETIRING: " + mQCode);
        CSMQBean.logger.info(CSMQBean.getProperty("DEFAULT_DRAFT_RELEASE_GROUP"));
        String sql = "{call NMAT_UI_pkg.reactivate_retire_nmq(?,?,?,?,?,?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        FacesMessage msg;
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "NMAT_UI_pkg.reactivate_retire_nmq");
        boolean retVal = false;
        try {
            cstmt.setString("pContent_ID", dictContentID);
            cstmt.setString("pContent_code", mQCode);
            cstmt.setString("pActivation_Group", CSMQBean.getProperty("DEFAULT_DRAFT_RELEASE_GROUP"));
            cstmt.setString("pStatus", CSMQBean.INACTIVE_ACTIVITY_STATUS);
            cstmt.setString("pUserRole", userRole);
            cstmt.setString("pUserName", userStatus);
            cstmt.executeUpdate();
            cstmt.close();
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "MedDRA Query Retired Successfully", null);
            retVal = true;
        } catch (SQLException e) {
            if (e.getMessage().indexOf(CSMQBean.INVALID_PROMOTION_ERROR) > -1) {
                String messageText = CSMQBean.getProperty("DELETE_DURING_IMPACT_ANALYSYS");
                msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
            } else {
                CSMQBean.logger.error(e.getMessage());
                e.printStackTrace();
                msg =
new FacesMessage(FacesMessage.SEVERITY_ERROR, "Unable to Retire " + CSMQBean.customMQName, e.getMessage());
            }
            retVal = false;
        }

        FacesContext.getCurrentInstance().addMessage(null, msg);
        return retVal;
    }


    public static void setCurrentFilterDictionary(String shortName) {
        String sql = "{call smq_codelist_pkg.set_database_customizations (?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "smq_codelist_pkg.set_database_customizations");
        try {
            cstmt.setString(1, shortName);
            cstmt.executeUpdate();
            cstmt.close();
        } catch (Exception e) {
            CSMQBean.logger.error(e.getMessage());
            e.printStackTrace();
        }
    }


    public static Hashtable changeState(String dictContentIDs, String state, String currentUser,
                                        String currentUserRole, oracle.jbo.domain.Date dueDate, String comment,
                                        String activationGroup) {
        return changeState(dictContentIDs, state, currentUser, currentUserRole, dueDate, comment, activationGroup,
                           true);
    }


    public static Hashtable changeState(String dictContentIDs, String state, String currentUser,
                                        String currentUserRole, oracle.jbo.domain.Date dueDate, String comment,
                                        String activationGroup, boolean showMessage) {

        /*
        PROCEDURE workflow_pkg.change_state_of_nmq (
          i_dict_content_id IN tms_predict_contents.predict_content_id%TYPE,
          i_new_state       IN nmq_track_state.state%TYPE,
          i_user_id         IN nmq_track_state.created_user%TYPE,
          i_due_date        IN nmq_track_state.due_date_ts%TYPE,
          i_comment         IN nmq_track_state.description%TYPE,
          i_group_name      IN tms.tms_predict_groups.name%TYPE)
          o_new_state        OUT nmq_track_state.state%TYPE,
          o_approval_reason  OUT nmq_track_state.description%TYPE);

        */

        CSMQBean.logger.info("*** CHANGING STATE ***");
        CSMQBean.logger.info("i_dict_content_id: " + dictContentIDs);
        CSMQBean.logger.info("i_new_state: " + state);
        CSMQBean.logger.info("i_user_id: " + currentUser);
        CSMQBean.logger.info("i_due_date: " + dueDate);
        CSMQBean.logger.info("i_comment: " + comment);
        CSMQBean.logger.info("i_group_name: " + activationGroup);


        String sql = "{call workflow_pkg.change_state_of_nmq(?,?,?,?,?,?,?,?,?,?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        FacesMessage msg;
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "workflow_pkg.change_state_of_nmq");
        String messageText;
        Hashtable retVal = new Hashtable(); // array to return the new state and message

        try {
            cstmt.setString("i_dict_content_id", dictContentIDs);
            cstmt.setString("i_new_state", state);
            cstmt.setString("i_user_id", currentUser);
            cstmt.setString("i_role_name", currentUserRole);

            if (dueDate == null)
                cstmt.setNull("i_due_date", Types.DATE);
            else
                cstmt.setDate("i_due_date", dueDate.dateValue());

            cstmt.setString("i_comment", comment);
            cstmt.setString("i_group_name", activationGroup);
            cstmt.setNull("i_transaction_date", Types.DATE);

            cstmt.setString("o_new_state", "");
            cstmt.registerOutParameter("o_new_state", Types.NVARCHAR);

            cstmt.setString("o_approval_reason", "");
            cstmt.registerOutParameter("o_approval_reason", Types.NVARCHAR);

            cstmt.executeUpdate();

            String tempState = cstmt.getString("o_new_state");
            String tempReason = cstmt.getString("o_approval_reason");

            if (tempState != null)
                retVal.put("STATE", tempState);
            if (tempReason != null)
                retVal.put("REASON", tempReason);

            cstmt.close();
            msg =
new FacesMessage(FacesMessage.SEVERITY_INFO, "MedDRA Query State Changed Successfully to " + state, null);
        } catch (SQLException e) {
            e.printStackTrace();

            if (e.getMessage().indexOf(CSMQBean.INVALID_PROMOTION_ERROR) > -1) {
                messageText = CSMQBean.getProperty("INVALID_PROMOTION_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_PROMOTION_SEQUENCE_ERROR) > -1) {
                messageText = CSMQBean.getProperty("INVALID_PROMOTION_SEQUENCE_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.PROMOTION_DEPENDENCY_ERROR) > -1) {
                messageText = CSMQBean.getProperty("PROMOTION_DEPENDENCY_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_STATE_CHANGE_ERROR) > -1) {
                messageText = e.getMessage(); //CSMQBean.getProperty("INVALID_STATE_CHANGE_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.MUST_BE_NMQ_OR_SMQ_ERROR) > -1) {
                messageText = CSMQBean.getProperty("MUST_BE_NMQ_OR_SMQ_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.GENERIC_ACTIVATION_ERROR) > -1) {
                messageText = CSMQBean.getProperty("GENERIC_ACTIVATION_ERROR") + "DETAILS: " + e.getMessage();
            } else if (e.getMessage().indexOf(CSMQBean.DATABASE_CONFIGURATION_ERROR) > -1) {
                messageText = CSMQBean.getProperty("DATABASE_CONFIGURATION_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_STATE_CHANGE_FROM_PENDING_TO_DRAFT_ERROR) > -1) {
                messageText = CSMQBean.getProperty("INVALID_STATE_CHANGE_FROM_PENDING_TO_DRAFT_ERROR");
            }

            else { // it's something else
                messageText = "The following error occurred: " + e.getMessage();
                e.printStackTrace();
            }
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
        if (showMessage)
            FacesContext.getCurrentInstance().addMessage(null, msg);
        return retVal;
    }


    public static Hashtable changeStateFromDraftToPublish(String dictContentIDs, String state, String currentUser,
                                                          String currentUserRole, oracle.jbo.domain.Date dueDate,
                                                          String comment, String activationGroup) {
        /*
        FUNCTION promote_to_published
                (pDictContentID  IN NUMBER,
                 pConfirmMsg    OUT VARCHAR2)
              RETURN VARCHAR;
        */
        state = "Published";
        CSMQBean.logger.info("*** CHANGING STATE FROM DRAFT TO PUBLISH ***");
        CSMQBean.logger.info("i_dict_content_id: " + dictContentIDs);
        CSMQBean.logger.info("i_new_state: " + state);
        CSMQBean.logger.info("i_user_id: " + currentUser);
        CSMQBean.logger.info("i_due_date: " + dueDate);
        CSMQBean.logger.info("i_comment: " + comment);
        CSMQBean.logger.info("i_group_name: " + activationGroup);

        String sql = "{call CQT_WHOD_UI_TMS_UTILS.promote_to_published(?,?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        FacesMessage msg;
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "CQT_WHOD_UI_TMS_UTILS.promote_to_published");
        String messageText;
        Hashtable retVal = new Hashtable(); // array to return the new state and message

        try {
            cstmt.setString("pDictContentID", dictContentIDs);
            cstmt.registerOutParameter("pConfirmMsg", Types.VARCHAR);
            cstmt.executeUpdate();
            String confirmMsg = cstmt.getString("pConfirmMsg");
            if (confirmMsg != null)
                retVal.put("ConfirmMsg", confirmMsg);

            cstmt.close();
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, confirmMsg, null);
        } catch (SQLException e) {
            e.printStackTrace();

            if (e.getMessage().indexOf(CSMQBean.INVALID_PROMOTION_ERROR) > -1) {
                messageText = CSMQBean.getProperty("INVALID_PROMOTION_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_PROMOTION_SEQUENCE_ERROR) > -1) {
                messageText = CSMQBean.getProperty("INVALID_PROMOTION_SEQUENCE_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.PROMOTION_DEPENDENCY_ERROR) > -1) {
                messageText = CSMQBean.getProperty("PROMOTION_DEPENDENCY_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_STATE_CHANGE_ERROR) > -1) {
                messageText = e.getMessage(); //CSMQBean.getProperty("INVALID_STATE_CHANGE_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.MUST_BE_NMQ_OR_SMQ_ERROR) > -1) {
                messageText = CSMQBean.getProperty("MUST_BE_NMQ_OR_SMQ_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.GENERIC_ACTIVATION_ERROR) > -1) {
                messageText = CSMQBean.getProperty("GENERIC_ACTIVATION_ERROR") + "DETAILS: " + e.getMessage();
            } else if (e.getMessage().indexOf(CSMQBean.DATABASE_CONFIGURATION_ERROR) > -1) {
                messageText = CSMQBean.getProperty("DATABASE_CONFIGURATION_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_STATE_CHANGE_FROM_PENDING_TO_DRAFT_ERROR) > -1) {
                messageText = CSMQBean.getProperty("INVALID_STATE_CHANGE_FROM_PENDING_TO_DRAFT_ERROR");
            } else { // it's something else
                messageText = "The following error occurred: " + e.getMessage();
                e.printStackTrace();
            }
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return retVal;
    }


    public static Hashtable setDefaultIAState(String dictContentIDs, String nmqSmq, String currentUser,
                                              String currentUserRole) {

        /*
       PROCEDURE default_state_to_pending_ia (
         i_dict_content_id  IN VARCHAR2,
         i_nmq_smq          IN VARCHAR2,
         i_new_state        IN nmq_track_state.state%TYPE := g_state_pending,
         i_user_id          IN nmq_track_state.created_user%TYPE,
         i_role_name        IN user_role_privs.granted_role%TYPE,
         i_due_date         IN nmq_track_state.due_date_ts%TYPE := NULL,
         i_comment          IN nmq_track_state.description%TYPE := NULL,
         i_group_name       IN tms.tms_predict_groups.NAME%TYPE := NULL,
         i_transaction_date IN nmq_track_state.creation_ts%TYPE := NULL,
         o_new_state        OUT nmq_track_state.state%TYPE,
         o_approval_reason  OUT nmq_track_state.description%TYPE);
        */

        CSMQBean.logger.info("*** CHANGING STATE ***");
        CSMQBean.logger.info("i_dict_content_id: " + dictContentIDs);
        CSMQBean.logger.info("i_nmq_smq: " + nmqSmq);
        CSMQBean.logger.info("i_user_id: " + currentUser);
        CSMQBean.logger.info("i_role_name: " + currentUserRole);

        String sql = "{call workflow_pkg.default_state_to_pending_ia(?,?,?,?,?,?,?,?,?,?,?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        FacesMessage msg;
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "workflow_pkg.default_state_to_pending_ia");
        String messageText;
        Hashtable retVal = new Hashtable(); // array to return the new state and message

        try {
            cstmt.setString("i_dict_content_id", dictContentIDs);
            cstmt.setString("i_nmq_smq", nmqSmq);
            cstmt.setString("i_user_id", currentUser);
            cstmt.setString("i_role_name", currentUserRole);
            cstmt.setNull("i_new_state", Types.VARCHAR);
            cstmt.setNull("i_due_date", Types.DATE);
            cstmt.setNull("i_comment", Types.VARCHAR);
            cstmt.setNull("i_group_name", Types.VARCHAR);
            cstmt.setNull("i_transaction_date", Types.DATE);

            cstmt.setString("o_new_state", "");
            cstmt.registerOutParameter("o_new_state", Types.VARCHAR);

            cstmt.setString("o_approval_reason", "");
            cstmt.registerOutParameter("o_approval_reason", Types.VARCHAR);

            cstmt.executeUpdate();

            String tempState = cstmt.getString("o_new_state");
            String tempReason = cstmt.getString("o_approval_reason");

            if (tempState != null)
                retVal.put("STATE", tempState);
            if (tempReason != null)
                retVal.put("REASON", tempReason);
            cstmt.close();
            return retVal;
        } catch (SQLException e) {
            CSMQBean.logger.error(null, e);
            return null;
        }

    }


    /*
     * @author MTW
     * 06/30/2014
     * @fsds NMAT-UC01.02 & NMAT-UC11.02
     * add String currentDesignee
     */

    public static Hashtable saveDetails(String currentFilterDictionaryShortName, String currentPredictGroups,
                                        String tempName, String currentProduct, String currentTermLevel,
                                        String currentScope, String currentMQALGO, String currentMQCRTEV,
                                        String currentMQGROUP, String currentContentCode, String updateParam,
                                        String currentRequestor, String currentDictContentID, String userRole,
                                        String action, String currentPendingStatus, String currentDesignee) {

        // CALL PROC TO SAVE
        /*
        PROCEDURE nmat_ui_pkg.load_content (
            psmq_dct_nm         IN tms.tms_def_dictionaries.short_name%TYPE,
            psmq_grp_nm         IN tms.tms_predict_groups.name%TYPE,
            pmq_name            IN tms.tms_predict_contents.term%TYPE,
            pmq_product         IN tms.tms_predict_contents.value_3%TYPE,
            pmq_level           IN tms.tms_def_levels.name%TYPE,
            pmq_scp             IN tms.tms_predict_contents.category%TYPE,
            pmq_algo            IN tms.tms_predict_contents.value_1%TYPE,
            pmq_crtev           IN tms.tms_predict_contents.value_4%TYPE,
            pmq_group           IN tms.tms_predict_contents.value_2%TYPE,
            pmqcd               IN tms.tms_predict_contents.dict_content_code%TYPE,
            pstatus             IN tms.tms_predict_contents.status%TYPE, --I, U
            pUserName           IN tms.tms_predict_contents.created_by%TYPE,
            pContentID          IN tms.tms_predict_contents.predict_content_id%TYPE,
            pUserRole           IN  VARCHAR2,
            pCurrPendStatus     IN  VARCHAR2,
            o_dict_content_id   OUT tms_predict_contents.predict_content_id%TYPE,
            o_dict_content_code OUT tms_predict_contents.dict_content_code%TYPE,
            o_Create_date       OUT tms_predict_contents.dict_content_entry_ts%TYPE
            )
        */

        String sql = "{call nmat_ui_pkg.load_content(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        String newDictContentID = "";
        String newDictContentCode = "";
        oracle.jbo.domain.Date currentDateRequested = null;
        Hashtable retVal = new Hashtable(); // array to return the new state and message

        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "nmat_ui_pkg.load_content");

        try {
            cstmt.setString("psmq_dct_nm", currentFilterDictionaryShortName);
            cstmt.setString("psmq_grp_nm", currentPredictGroups);
            cstmt.setString("pmq_name", tempName);
            cstmt.setString("pmq_product", currentProduct);
            cstmt.setString("pmq_level", currentTermLevel);
            cstmt.setString("pmq_scp", currentScope);
            cstmt.setString("pmq_algo", currentMQALGO);
            cstmt.setString("pmq_crtev", currentMQCRTEV);
            cstmt.setString("pmq_group", currentMQGROUP);
            cstmt.setString("pmqcd", currentContentCode);
            cstmt.setString("pstatus", updateParam);
            cstmt.setString("pUserName", currentRequestor);
            cstmt.setString("pContentID", currentDictContentID);
            cstmt.setString("pUserRole", userRole);
            cstmt.setString("pCurrPendStatus", currentPendingStatus);
            cstmt.setString("o_dict_content_id", "");
            cstmt.registerOutParameter("o_dict_content_id", Types.NVARCHAR);
            cstmt.setString("o_dict_content_code", "");
            cstmt.registerOutParameter("o_dict_content_code", Types.NVARCHAR);
            cstmt.setString("o_Create_date", "");
            cstmt.registerOutParameter("o_Create_date", Types.NVARCHAR);

            /*
             * @author MTW
             * 07/22/2014
             */
            //saveDesignee(currentDictContentID, currentDesignee);
            /*
             * TES TEMP CHANGE TO CALL FUNCTION BELOW
             *
             */


            cstmt.executeUpdate();
            newDictContentID = cstmt.getString("o_dict_content_id");
            newDictContentCode = cstmt.getString("o_dict_content_code");
            String tempDate = cstmt.getString("o_Create_date");

            updateMQDesignee(newDictContentID, currentDesignee);

            //DateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
            String dateFormat = "dd-MMM-yy";
            DateFormat formatter = new SimpleDateFormat(dateFormat);
            java.util.Date date = null;
            //java.util.Date date = formatter.parse(tempDate);
            //20141121-2  fix -Venkat
            try {
                date = formatter.parse(tempDate);
            } catch (java.text.ParseException e) {
                dateFormat = "dd-MM-yy";
                formatter = new SimpleDateFormat(dateFormat);
                date = formatter.parse(tempDate);
            }
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            currentDateRequested = new oracle.jbo.domain.Date(sqlDate);
            cstmt.close();

            if (newDictContentID != null)
                retVal.put("NEW_DICT_CONTENT_ID", newDictContentID);
            if (newDictContentCode != null)
                retVal.put("NEW_DICT_CONTENT_CODE", newDictContentCode);
            if (currentDateRequested != null)
                retVal.put("CURRENT_DATE_REQUESTED", currentDateRequested);
        } catch (SQLException e) {
            String messageText;
            if (e.getMessage().indexOf(CSMQBean.NAME_IN_USE_ERROR) > -1) { // it's a name already in use error
                messageText = "The name: " + tempName + " is already in use.  Please use another name";
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_STATE_CHANGE_ERROR) >
                       -1) { // it's a name already in use error
                messageText =
                        tempName + " is Pending Impact Assessment and must be deleted in Impact Assessment to Update the Current NMQ.";
            } else { // it's something else
                messageText =
                        "The following error occurred.  " + tempName + " was not " + action + " successfully.\n" +
                        e.getMessage();
                e.printStackTrace();
            }
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } catch (ParseException e) {
            CSMQBean.logger.error(null, e);
        }
        return retVal;
    }

    private static void saveDesignee(String currentDictContentID, String currentDesignee) {
        /*
         * TODO: IMPLEMENT THIS IN DATABASE INSTEAD OF CALLING SQL BELOW
         *
         */
        /*
         PROCEDURE NMAT_UI_PKG.s_insert_update_designee (
            pPredictConID      IN tms.tms_predict_contents.predict_content_id%TYPE,
            pDesignee          IN track_designee.designee%TYPE
        )
        */

        String sql = "{call NMAT_UI_PKG.s_insert_update_designee(?,?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        String newDictContentID = "";
        String newDictContentCode = "";
        Hashtable retVal = new Hashtable(); // array to return the new state and message

        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "NMAT_UI_PKG.s_insert_update_designee");

        try {
            cstmt.setString("pPredictConID", currentDictContentID);
            cstmt.setString("pDesignee", currentDesignee);

            cstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //        catch (ParseException e) {
        //            CSMQBean.logger.error(null,e);
        //        }
    }

    public static Hashtable saveIADetails(String tempName, String currentProduct, String currentTermLevel,
                                          String currentScope, String currentMQALGO, String currentMQCRTEV,
                                          String currentMQGROUP, String currentContentCode, String updateParam,
                                          String currentRequestor, String currentDictContentID, String userRole,
                                          String action) {

        // CALL PROC TO SAVE
        /*
       PROCEDURE nmat_ui_pkg.load_content_IA
         (pmq_name            IN tms.tms_predict_contents.term%TYPE,
          pmq_product         IN tms.tms_predict_contents.value_3%TYPE,
          pmq_level           IN tms.tms_def_levels.name%TYPE,
          pmq_scp             IN tms.tms_predict_contents.category%TYPE,
          pmq_algo            IN tms.tms_predict_contents.value_1%TYPE,
          pmq_crtev           IN tms.tms_predict_contents.value_4%TYPE,
          pmq_group           IN tms.tms_predict_contents.value_2%TYPE,
          pmqcd               IN tms.tms_predict_contents.dict_content_code%TYPE,
          pstatus             IN tms.tms_predict_contents.status%TYPE, --I, U
          pUserName           IN tms.tms_predict_contents.created_by%TYPE,
          pContentID          IN tms.tms_predict_contents.predict_content_id%TYPE,
          pUserRole           IN  VARCHAR2,
          o_dict_content_id   OUT tms_predict_contents.predict_content_id%TYPE,
          o_dict_content_code OUT tms_predict_contents.dict_content_code%TYPE,
          o_Create_date       OUT tms_predict_contents.dict_content_entry_ts%TYPE
         ) ;
        */

        String sql = "{call nmat_ui_pkg.load_content_IA(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        String newDictContentID = "";
        String newDictContentCode = "";
        oracle.jbo.domain.Date currentDateRequested = null;
        Hashtable retVal = new Hashtable(); // array to return the new state and message

        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "nmat_ui_pkg.load_content_IA");

        try {
            cstmt.setString("pmq_name", tempName);
            cstmt.setString("pmq_product", currentProduct);
            cstmt.setString("pmq_level", currentTermLevel);
            cstmt.setString("pmq_scp", currentScope);
            cstmt.setString("pmq_algo", currentMQALGO);
            cstmt.setString("pmq_crtev", currentMQCRTEV);
            cstmt.setString("pmq_group", currentMQGROUP);
            cstmt.setString("pmqcd", currentContentCode);
            cstmt.setString("pstatus", updateParam);
            cstmt.setString("pUserName", currentRequestor);
            cstmt.setString("pContentID", currentDictContentID);
            cstmt.setString("pUserRole", userRole);
            cstmt.setString("o_dict_content_id", "");
            cstmt.registerOutParameter("o_dict_content_id", Types.NVARCHAR);
            cstmt.setString("o_dict_content_code", "");
            cstmt.registerOutParameter("o_dict_content_code", Types.NVARCHAR);
            cstmt.setString("o_Create_date", "");
            cstmt.registerOutParameter("o_Create_date", Types.NVARCHAR);

            cstmt.executeUpdate();
            newDictContentID = cstmt.getString("o_dict_content_id");
            newDictContentCode = cstmt.getString("o_dict_content_code");
            String tempDate = cstmt.getString("o_Create_date");

            DateFormat formatter = new SimpleDateFormat("dd-MMM-yy");
            java.util.Date date = formatter.parse(tempDate);
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            currentDateRequested = new oracle.jbo.domain.Date(sqlDate);
            cstmt.close();

            if (newDictContentID != null)
                retVal.put("NEW_DICT_CONTENT_ID", newDictContentID);
            if (newDictContentCode != null)
                retVal.put("NEW_DICT_CONTENT_CODE", newDictContentCode);
            if (currentDateRequested != null)
                retVal.put("CURRENT_DATE_REQUESTED", currentDateRequested);
        } catch (SQLException e) {
            String messageText;
            if (e.getMessage().indexOf(CSMQBean.NAME_IN_USE_ERROR) > -1) { // it's a name already in use error
                messageText = "The name: " + tempName + " is already in use.  Please use another name";
            } else { // it's something else
                messageText =
                        "The following error occurred.  " + tempName + " was not " + action + " successfully.\n" +
                        e.getMessage();
                e.printStackTrace();
            }
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } catch (ParseException e) {
            CSMQBean.logger.error(null, e);
        }
        return retVal;
    }


    public static int saveInfNotes(String noteName, String content, String currentFilterDictionaryShortName,
                                   String currentPredictGroups, String currentDictContentID, String currentTermLevel,
                                   String userName, String userRole, String action, String extension) {
        /*
        PROCEDURE nmat_ui_pkg.load_notes
             (psmq_dct_nm        IN tms.tms_def_dictionaries.short_name%TYPE,
              psmq_grp_nm        IN tms.tms_predict_groups.name%TYPE,
              pContent_Id        IN tms.tms_predict_contents.predict_content_id%TYPE,
              pmq_level          IN tms.tms_def_levels.name%TYPE,
              pnote_nm           IN tms.tms_def_details.short_name%TYPE, --SMQDESC
              pnote_type         IN tms.tms_def_dict_info_dets.info_note_type%TYPE,--C=Content; R=Relations; D=Dictionary
              pnote_val          IN tms.tms_dict_info_clobs.def_detail_value%TYPE,
              pUserRole          IN VARCHAR2,
              pUserName          IN nmq_track_state.created_user%TYPE,
              pTermType          IN VARCHAR2
              ) ;


        */
        CSMQBean.logger.info("SAVING NOTE: " + noteName);

        String sql = "{call nmat_ui_pkg.load_notes (?,?,?,?,?,?,?,?,?,?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        String newDictContentCode = "";
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "nmat_ui_pkg.load_notes");
        try {
            cstmt.setString("psmq_dct_nm", currentFilterDictionaryShortName);
            cstmt.setString("psmq_grp_nm", currentPredictGroups);
            cstmt.setString("pContent_Id", currentDictContentID);
            cstmt.setString("pmq_level", currentTermLevel);
            cstmt.setString("pnote_nm", noteName);
            cstmt.setString("pnote_type", "C");
            cstmt.setString("pnote_val", content);
            cstmt.setString("pUserRole", userRole);
            cstmt.setString("pUserName", userName);
            cstmt.setString("pTermType", extension);
            cstmt.executeUpdate();
            cstmt.close();
        } catch (SQLException e) {
            String messageText = "An error occurred.  " + noteName + " was not " + action + " successfully.";
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            e.printStackTrace();
            return 1;
        }
        return 0;
    }


    public static int saveIAInfNotes(String noteName, String content, String currentDictContentID,
                                     String currentTermLevel, String userName, String userRole, String action,
                                     String extension) {
        /*
        PROCEDURE nmat_ui_pkg.load_notes
             (psmq_dct_nm        IN tms.tms_def_dictionaries.short_name%TYPE,
              psmq_grp_nm        IN tms.tms_predict_groups.name%TYPE,
              pContent_Id        IN tms.tms_predict_contents.predict_content_id%TYPE,
              pmq_level          IN tms.tms_def_levels.name%TYPE,
              pnote_nm           IN tms.tms_def_details.short_name%TYPE, --SMQDESC
              pnote_type         IN tms.tms_def_dict_info_dets.info_note_type%TYPE,--C=Content; R=Relations; D=Dictionary
              pnote_val          IN tms.tms_dict_info_clobs.def_detail_value%TYPE,
              pUserRole          IN VARCHAR2,
              pUserName          IN nmq_track_state.created_user%TYPE,
              pTermType          IN VARCHAR2
              ) ;


        */
        CSMQBean.logger.info("SAVING IA NOTE: " + noteName);

        String sql = "{call nmat_ui_pkg.load_notes_IA (?,?,?,?,?,?,?,?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        String newDictContentCode = "";
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "nmat_ui_pkg.load_notes_IA");
        try {
            cstmt.setString("pContent_Id", currentDictContentID);
            cstmt.setString("pmq_level", currentTermLevel);
            cstmt.setString("pnote_nm", noteName);
            cstmt.setString("pnote_type", "C");
            cstmt.setString("pnote_val", content);
            cstmt.setString("pUserRole", userRole);
            cstmt.setString("pUserName", userName);
            cstmt.setString("pTermType", extension);
            cstmt.executeUpdate();
            cstmt.close();
        } catch (SQLException e) {
            String messageText = "An error occurred.  " + noteName + " was not " + action + " successfully.";
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            e.printStackTrace();
            return 1;
        }
        return 0;
    }


    public static void activateGroup(String groupName) {
        /*
             PROCEDURE nmat_ui_pkg.nmat_activation_chkmode
                (pGrpName IN tms.tms_predict_groups.Name%TYPE)
            */
        String messageText;
        FacesMessage msg;
        String sql = "{call nmat_ui_pkg.nmat_transid_activation (?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "nmat_ui_pkg.nmat_transid_activation");
        try {
            cstmt.setString(1, groupName);
            cstmt.executeUpdate();
            cstmt.close();
            msg =
new FacesMessage(FacesMessage.SEVERITY_INFO, "Activation group " + groupName + " successfully activated", null);
        } catch (SQLException e) {
            if (e.getMessage().indexOf(CSMQBean.CHECK_MODE_ERRORS) > -1) { // it's a name already in use error
                messageText =
                        "Errors occurred during the pre-activation validation and need to be resolved before " + groupName +
                        " can be activated.";
            } else { // it's something else
                messageText = "The following error occurred: " + e.getMessage();
                e.printStackTrace();
            }
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }

    public static boolean activateGroupInCheckMode(String groupName) {
        /*
             PROCEDURE nmat_ui_pkg.nmat_transid_activation
            (pGrpName IN tms.tms_predict_groups.name%TYPE)
            */
        boolean retVal = false;
        String messageText;
        FacesMessage msg;
        String sql = "{call nmat_ui_pkg.nmat_activation_chkmode (?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "nmat_ui_pkg.nmat_activation_chkmode");
        try {
            cstmt.setString(1, groupName);
            cstmt.executeUpdate();
            cstmt.close();
            msg =
new FacesMessage(FacesMessage.SEVERITY_INFO, "Activation group " + groupName + " successfully validated in checkmode.",
                 null);
            retVal = true;
        } catch (SQLException e) {
            if (e.getMessage().indexOf(CSMQBean.ACTIVATION_ERRORS) > -1) {
                messageText =
                        "The errors found during CHECK mode activation are displayed on the screen. Demote the NMQs in error to Draft to resolve, Promote and Validate again.";
            } else if (e.getMessage().indexOf(CSMQBean.RECORD_LOCKED_ERROR) > -1) {
                messageText = CSMQBean.getProperty("RECORD_LOCKED_ERROR");
            } else { // it's something else
                messageText = "The following error occurred: " + e.getMessage();
                e.printStackTrace();
            }
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
            retVal = false;
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return retVal;
    }


    public static void refreshImpactMVs() {
        /*
           PROCEDURE impact_pkg.submit_refresh_mviews(
             i_filter_dict_name IN tms_def_dictionaries.short_name%TYPE)
            */
        String messageText = "";
        FacesMessage msg;
        //String sql = "{call impact_pkg.submit_refresh_mviews (?)}";
        //String sql = "DECLARE v_job number; begin dbms_job.submit (job => v_job,next_date => SYSDATE,what => 'BEGIN smq_codelist_pkg.define_globals; ' || 'dbms_mview.refresh(''nmq_smq_impact_contents''); END;',INTERVAL => NULL);exception WHEN others THEN raise; end;";
        String sql = "BEGIN impact_pkg.submit_refresh_mviews; EXCEPTION WHEN OTHERS THEN RAISE; END;";


        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "impact_pkg.submit_refresh_mviews");
        CSMQBean.logger.info("*** SUBMITTING JOB TO REFRESH VIEWS ***");
        try {
            //cstmt.setString(1, CSMQBean.defaultFilterDictionaryShortName);
            cstmt.executeUpdate();
            cstmt.close();
            msg =
new FacesMessage(FacesMessage.SEVERITY_INFO, "Impacted list refreshed for dictionary: " + CSMQBean.defaultFilterDictionaryShortName +
                 " successfully activated", null);
        } catch (SQLException e) {
            if (e.getMessage().indexOf(CSMQBean.CHECK_MODE_ERRORS) > -1) { // it's a name already in use error
                messageText =
                        "The following error(s) occurred during the the refresh of the impacted list for dictionary: " +
                        CSMQBean.defaultFilterDictionaryShortName + "\n" +
                        e.getMessage();
            }

            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
    }


    public static Hashtable<String, String> getActivationInfo(String dictContentID, String dictionaryID) {
        /*
        PROCEDURE get_activation_info
                (pdictContentID         IN  NUMBER,
                 pInitialCreationDate   OUT DATE,
                 pInitialCreationBy     OUT VARCHAR2,
                 pLastActivationDate    OUT DATE,
                 pActivationBy          OUT VARCHAR2)
        */

        CSMQBean.logger.info("*** GETTING ACTIVATION INFO ***");
        CSMQBean.logger.info("pDictionaryId: " + dictionaryID);
        CSMQBean.logger.info("pdictContentID: " + dictContentID);

        String sql = "{call cqt_whod_ui_tms_utils.get_activation_info(?,?,?,?,?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "cqt_whod_ui_tms_utils.get_activation_info");
        Hashtable<String, String> retVal =
            new Hashtable<String, String>(); // array to return the new state and message

        try {
            cstmt.setString(1, dictContentID);
            cstmt.registerOutParameter(2, oracle.jdbc.OracleTypes.DATE);
            cstmt.registerOutParameter(3, OracleTypes.VARCHAR);
            cstmt.registerOutParameter(4, oracle.jdbc.OracleTypes.DATE);
            cstmt.registerOutParameter(5, oracle.jdbc.OracleTypes.VARCHAR);
            cstmt.executeUpdate();
            String initialCreationDate = cstmt.getString(2) != null ? cstmt.getString(2) : "";
            String initialCreationBy = cstmt.getString(3) != null ? cstmt.getString(3) : "";
            String lastActivationDate = cstmt.getString(4) != null ? cstmt.getString(4) : "";
            String activationBy = cstmt.getString(5) != null ? cstmt.getString(5) : "";
            retVal.put("initialCreationDate", initialCreationDate);
            retVal.put("initialCreationBy", initialCreationBy);
            retVal.put("lastActivationDate", lastActivationDate);
            retVal.put("activationBy", activationBy);
            cstmt.close();
            return retVal;
        } catch (SQLException e) {
            //messageText = "The following error occurred: " +  e.getMessage() ;
            e.printStackTrace();
        }
        //msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
        //FacesContext.getCurrentInstance().addMessage(null, msg);
        return null;
    }


    public static boolean updateMQDesignee(String dictContentID, String designee) {
        /*
        PROCEDURE nmat_ui_pkg.s_insert_update_designee
                   (pPredictConID             IN tms.tms_predict_contents.predict_content_id%TYPE,
                   pDesignee           IN track_designee.designee%TYPE)
        */

        String sql = "{call nmat_ui_pkg.s_insert_update_designee(?,?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();

        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "nmat_ui_pkg.s_insert_update_designee");

        try {
            cstmt.setString(1, dictContentID);
            cstmt.setString(2, designee);
            cstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static Map<String, Object> getPageFlowScope() {
        return AdfFacesContext.getCurrentInstance().getPageFlowScope();
    }

    public static WhodSourceTermSearchBean getWhodSourceTermSearchBean() {
        WhodSourceTermSearchBean whodSourceTermSearchBean =
            (WhodSourceTermSearchBean)getPageFlowScope().get("WhodSourceTermSearchBean");
        return whodSourceTermSearchBean;
    }

    public static WhodWizardBean getWhodWizardBean() {
        WhodWizardBean whodWizardBean = (WhodWizardBean)getPageFlowScope().get("WhodWizardBean");
        return whodWizardBean;
    }

    public static WhodWizardSearchBean getWhodWizardSearchBean() {
        WhodWizardSearchBean whodWizardSearchBean =
            (WhodWizardSearchBean)getPageFlowScope().get("WhodWizardSearchBean");
        return whodWizardSearchBean;
    }

    public static void clearRelations() {
        getWhodSourceTermSearchBean().clearTree();
        getWhodWizardBean().clearRelations();
    }

    public static UserBean getUserBean() {
        UserBean userBean = (UserBean)ADFUtils.getSessionScope().get("UserBean");
        return userBean;
    }

    public static CSMQBean getCSMQBean() {
        CSMQBean cSMQBean = (CSMQBean)ADFContext.getCurrent().getApplicationScope().get("CSMQBean");
        return cSMQBean;
    }

    public static Hashtable saveDetails(String levelName, String levelExtension, String approvedFlag, String termName,
                                        String dGScopeFlag, String dGActiveStatus, String dGProductLIST,
                                        String dGGroupLIST, String commentText, String designee, String userRole,
                                        String action) {
        System.out.println("saveDetails() designee==" + designee);
        // CALL PROC TO SAVE
        /*
         * cqt_whod_ui_tms_utils
        FUNCTION insert_content_data
               (pLevelName           IN  VARCHAR2,
                pLevelExtension      IN  VARCHAR2,
                pTerm                IN  VARCHAR2,
                pApprovedFlag        IN  VARCHAR2  DEFAULT 'Y',
                pDGScopeFlag         IN  VARCHAR2  DEFAULT 'N',
                pDGActiveStatus      IN  VARCHAR2  DEFAULT 'A',
                pDGProductLIST       IN  VARCHAR2  DEFAULT NULL,
                pDGGroupLIST         IN  VARCHAR2  DEFAULT NULL,
                pCommentText         IN  VARCHAR2  DEFAULT NULL,
                pDesignee            IN  VARCHAR2  DEFAULT NULL,
                pPredictContentID    OUT NUMBER)
             RETURN VARCHAR2;
        */
        dGActiveStatus = "D"; //TODO need to remove this hard coding
        String sql = "{ ? = call cqt_whod_ui_tms_utils.insert_content_data(?,?,?,?,?,  ?,?,?,?,?,  ?,?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        Integer newDictContentID = null;
        Hashtable retVal = new Hashtable(); // array to return the new state and message
        try {
            CallableStatement cstmt =
                new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                      "cqt_whod_ui_tms_utils.insert_content_data");
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.setString(2, levelName);
            cstmt.setString(3, levelExtension);
            cstmt.setString(4, termName);
            cstmt.setString(5, approvedFlag);
            cstmt.setString(6, dGScopeFlag);
            cstmt.setString(7, dGActiveStatus);
            cstmt.setString(8, dGProductLIST);
            cstmt.setString(9, dGGroupLIST);
            cstmt.setString(10, commentText);
            cstmt.setString(11, designee);
            cstmt.registerOutParameter(12, Types.INTEGER);
            cstmt.registerOutParameter(13, Types.VARCHAR);
            cstmt.executeQuery();
            newDictContentID = cstmt.getInt(12);
            String newDictContentCode = cstmt.getString(13);
            cstmt.close();
            System.out.println("newDictContentID==" + newDictContentID + ";;; newDictContentCode ==" +
                               newDictContentCode);
            System.out.println();
            if (newDictContentID != null) {
                retVal.put("NEW_DICT_CONTENT_ID", "" + newDictContentID);
                retVal.put("NEW_DICT_CONTENT_CODE", newDictContentCode);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            String messageText;

            if (e.getMessage().indexOf(CSMQBean.NAME_IN_USE_ERROR) > -1) { // it's a name already in use error
                messageText = "The name: " + termName + " is already in use.  Please use another name";
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_STATE_CHANGE_ERROR) >
                       -1) { // it's a name already in use error
                messageText =
                        termName + " is Pending Impact Assessment and must be deleted in Impact Assessment to Update the Current NMQ.";
            } else { // it's something else
                messageText =
                        "The following error occurred.  " + termName + " was not " + action + " successfully.\n" +
                        e.getMessage();
                e.printStackTrace();
            }
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return retVal;
    }

    public static void getInfNotes(String currentDictContentID) {
        try {
            DCIteratorBinding dciterb = ADFUtils.findIterator("WHODInfoNotesIterator");
            ViewObject vo = dciterb.getViewObject();
            vo.setNamedWhereClauseParam("dDictContentId", currentDictContentID);
            vo.executeQuery();
            WhodWizardBean whodWizardBean = WhodUtils.getWhodWizardBean();
            if (vo.getRowCount() > 0) {
                while (vo.hasNext()) {
                    WHODInfoNotesVORowImpl row = (WHODInfoNotesVORowImpl)vo.next();
                    if (row.getInfoNoteType() != null && row.getInfoNoteType().equals("DESC"))
                        whodWizardBean.setCurrentInfNoteDescription(Utils.getAsString(row, "InfoNoteValue"));
                    if (row.getInfoNoteType() != null && row.getInfoNoteType().equals("NOTE"))
                        whodWizardBean.setCurrentInfNoteNotes(Utils.getAsString(row, "InfoNoteValue"));
                }
            }
        } catch (java.util.NoSuchElementException e) {
            CSMQBean.logger.error(e.getMessage(), e);
        }
    }

    public static Hashtable saveUpdatedDetails(String currentDictContentID, String levelName, String levelExtension,
                                               String approvedFlag, String termName, String dGScopeFlag,
                                               String dGActiveStatus, String dGProductLIST, String dGGroupLIST,
                                               String commentText, String designee, String userRole, String action,
                                               String state) {
        System.out.println("saveUpdatedDetails() designee==" + designee);
        dGActiveStatus = "A"; //TODO need to remove this hard coding
        String sql = "{ ? = call cqt_whod_ui_tms_utils.update_content_data(?,?,?,?,?,  ?,?,?,?,?,  ?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        Integer newDictContentID = null;
        Hashtable retVal = new Hashtable(); // array to return the new state and message
        try {
            CallableStatement cstmt =
                new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                      "cqt_whod_ui_tms_utils.update_content_data");
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.setInt(2, new Integer(currentDictContentID));
            cstmt.setString(3, levelExtension);
            //cstmt.setString(3, levelExtension);
            cstmt.setString(4, termName);
            cstmt.setString(5, approvedFlag);
            cstmt.setString(6, dGScopeFlag);
            cstmt.setString(7, dGActiveStatus);
            cstmt.setString(8, dGProductLIST);
            cstmt.setString(9, dGGroupLIST);
            cstmt.setString(10, commentText);
            cstmt.setString(11, state);
            cstmt.setString(12, designee);
            //cstmt.registerOutParameter(13, Types.INTEGER);
            cstmt.executeQuery();
            //newDictContentID = cstmt.getInt(12);
            cstmt.close();
            System.out.println("newDictContentID==" + newDictContentID);
            if (newDictContentID != null)
                retVal.put("NEW_DICT_CONTENT_ID", "" + newDictContentID);
        } catch (SQLException e) {
            e.printStackTrace();
            String messageText;

            if (e.getMessage().indexOf(CSMQBean.NAME_IN_USE_ERROR) > -1) { // it's a name already in use error
                messageText = "The name: " + termName + " is already in use.  Please use another name";
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_STATE_CHANGE_ERROR) >
                       -1) { // it's a name already in use error
                messageText =
                        termName + " is Pending Impact Assessment and must be deleted in Impact Assessment to Update the Current NMQ.";
            } else { // it's something else
                messageText =
                        "The following error occurred.  " + termName + " was not " + action + " successfully.\n" +
                        e.getMessage();
                e.printStackTrace();
            }
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return retVal;
    }

    public static Hashtable saveNotesAndDescInfo(String dictContentId, String extension, String infoNotes,
                                                 String infoDesc, String infoSource) {
        CSMQBean.logger.info("saveNotesAndDescInfo(): dictContentId=" + dictContentId + ";; extension=" + extension +
                             ":; infoNotes=" + infoNotes + ":; infoDesc=" + infoDesc + ";;");
        Hashtable retVal = new Hashtable(); // array to return the new state and message
        String notesPredictInfoHdrID = null;
        if (infoNotes != null)
            notesPredictInfoHdrID = saveNoteInfo(dictContentId, "SDGNOTE", infoNotes);
        String descPredictInfoHdrID = null;
        if (infoDesc != null)
            descPredictInfoHdrID = saveNoteInfo(dictContentId, "SDGDESC", infoDesc);
        if (notesPredictInfoHdrID != null)
            retVal.put("NOTES_PRED_ID", notesPredictInfoHdrID);
        if (descPredictInfoHdrID != null)
            retVal.put("DESC_PRED_ID", descPredictInfoHdrID);
        return retVal;
    }


    public static String saveNoteInfo(String dictContentId, String infoNoteName, String infoNoteValue) {
        String sql = "{ ? = call cqt_whod_ui_tms_utils.process_info_note_data(?,?,?,?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        String predictInfoHdrID = null;

        try {
            CallableStatement cstmt =
                new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                      "cqt_whod_ui_tms_utils.process_info_note_data");
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.setInt(2, new Integer(dictContentId));
            cstmt.setString(3, infoNoteName);
            cstmt.setString(4, infoNoteValue);
            cstmt.registerOutParameter(5, Types.VARCHAR);
            cstmt.executeQuery();
            predictInfoHdrID = cstmt.getString(5);
            cstmt.close();
            System.out.println("predictInfoHdrID==" + predictInfoHdrID);
        } catch (SQLException e) {
            e.printStackTrace();
            String messageText = "An error occurred.  " + infoNoteName + " was not inserted successfully.";
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            e.printStackTrace();
            return new String("-1");
        } catch (Exception e) {
            e.printStackTrace();
            return new String("-1");
        }
        return predictInfoHdrID;
    }

    public static Hashtable copyDetails(String fromDictContentID, String newLevel, String newTerm) {
        System.out.println("copyDetails() fromDictContentID==" + fromDictContentID + ";; newTerm=" + newTerm +
                           ";; newLevel=" + newLevel);
        // CALL PROC TO SAVE
        /*
         * cqt_whod_ui_tms_utils
        FUNCTION copy_all_data
               (pCopyDictContentID   IN  NUMBER,
                pNewLevel            IN  VARCHAR2,
                pNewTerm             IN  VARCHAR2,
                pNewDictContentID    OUT NUMBER,
                pNewDictContentCode  OUT VARCHAR2,
                pConfirmMsg          OUT VARCHAR2)
             RETURN VARCHAR;
        */
        String sql = "{ ? = call cqt_whod_ui_tms_utils.copy_all_data(?,?,?,?,?,  ?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        Integer newDictContentID = null;
        Hashtable retVal = new Hashtable(); // array to return the new state and message
        CallableStatement cstmt = null;
        try {
            cstmt =
                new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                      "cqt_whod_ui_tms_utils.copy_all_data");
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.setInt(2, new Integer(fromDictContentID));
            cstmt.setString(3, newLevel);
            cstmt.setString(4, newTerm);
            cstmt.registerOutParameter(5, Types.INTEGER);
            cstmt.registerOutParameter(6, Types.VARCHAR);
            cstmt.registerOutParameter(7, Types.VARCHAR);
            cstmt.executeQuery();
            newDictContentID = cstmt.getInt(5);
            String newDictContentCode = cstmt.getString(6);
           // cstmt.close();
            System.out.println("newDictContentID==" + newDictContentID + ";;; newDictContentCode ==" +
                               newDictContentCode);
            System.out.println();
            if (newDictContentID != null) {
                retVal.put("NEW_DICT_CONTENT_ID", "" + newDictContentID);
                retVal.put("NEW_DICT_CONTENT_CODE", newDictContentCode);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            String messageText;

            if (e.getMessage().indexOf(CSMQBean.NAME_IN_USE_ERROR) > -1) { // it's a name already in use error
                messageText = "The name: " + newTerm + " is already in use.  Please use another name";
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_STATE_CHANGE_ERROR) >
                       -1) { // it's a name already in use error
                messageText =
                        newTerm + " is Pending Impact Assessment and must be deleted in Impact Assessment to Update the Current NMQ.";
            } else { // it's something else
                messageText = "The following error occurred.  " + newTerm + " was not copy successfully.\n" +
                        e.getMessage();
                e.printStackTrace();
            }
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != cstmt){
                try {
                    cstmt.close();
                }catch (SQLException e){
                    CSMQBean.logger.error("*** Error while closing cstmt ***");
                }
            }
        }
        return retVal;
    }

    public static String getDelimStr(List<String> items, char delim) {
        String delimStr = null;
        String temp = "";
        if (items != null && items.size() > 0) {
            for (String item : items) {
                temp = temp + item + delim;
            }
        }
        if (temp != null & temp.length() > 0)
            delimStr = temp.substring(0, temp.length() - 1);
        return delimStr;
    }

    public static Hashtable demoteToDraft(String dictContentIDs) {
        /*
        FUNCTION demote_to_draft
                (pDictContentID  IN NUMBER,
                 pConfirmMsg    OUT VARCHAR2)
              RETURN VARCHAR;
        */
        CSMQBean.logger.info("*** CHANGING STATE FROM DEMOTE TO DRAFT ***");
        CSMQBean.logger.info("i_dict_content_id: " + dictContentIDs);
        //String sql = "{? = call cqt_whod_ui_tms_utils.delete_content_data(?,?)}";
        String sql = "{? = call CQT_WHOD_UI_TMS_UTILS.demote_to_draft(?,?)}";
        //String sql = "{ ? = call cqt_whod_ui_tms_utils.copy_all_data(?,?,?,?,?,  ?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        FacesMessage msg;
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "CQT_WHOD_UI_TMS_UTILS.demote_to_draft");
        String messageText;
        Hashtable retVal = new Hashtable(); // array to return the new state and message

        try {
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.setString(2, dictContentIDs);
            cstmt.registerOutParameter(3, Types.VARCHAR);
            cstmt.executeUpdate();
            String confirmMsg = cstmt.getString(3);
            if (confirmMsg != null)
                retVal.put("ConfirmMsg", confirmMsg);

           // cstmt.close();
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, confirmMsg, null);
        } catch (SQLException e) {
            e.printStackTrace();

            if (e.getMessage().indexOf(CSMQBean.INVALID_PROMOTION_ERROR) > -1) {
                messageText = CSMQBean.getProperty("INVALID_PROMOTION_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_PROMOTION_SEQUENCE_ERROR) > -1) {
                messageText = CSMQBean.getProperty("INVALID_PROMOTION_SEQUENCE_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.PROMOTION_DEPENDENCY_ERROR) > -1) {
                messageText = CSMQBean.getProperty("PROMOTION_DEPENDENCY_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_STATE_CHANGE_ERROR) > -1) {
                messageText = e.getMessage(); //CSMQBean.getProperty("INVALID_STATE_CHANGE_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.MUST_BE_NMQ_OR_SMQ_ERROR) > -1) {
                messageText = CSMQBean.getProperty("MUST_BE_NMQ_OR_SMQ_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.GENERIC_ACTIVATION_ERROR) > -1) {
                messageText = CSMQBean.getProperty("GENERIC_ACTIVATION_ERROR") + "DETAILS: " + e.getMessage();
            } else if (e.getMessage().indexOf(CSMQBean.DATABASE_CONFIGURATION_ERROR) > -1) {
                messageText = CSMQBean.getProperty("DATABASE_CONFIGURATION_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_STATE_CHANGE_FROM_PENDING_TO_DRAFT_ERROR) > -1) {
                messageText = CSMQBean.getProperty("INVALID_STATE_CHANGE_FROM_PENDING_TO_DRAFT_ERROR");
            } else { // it's something else
                messageText = "The following error occurred: " + e.getMessage();
                e.printStackTrace();
            }
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } finally {
            if (null != cstmt){
                try {
                    cstmt.close();
                }catch (SQLException e){
                    CSMQBean.logger.error("*** Error while closing cstmt ***");
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return retVal;
    }

    public static Hashtable promoteAllToPublished() {
        /*
    PROCEDURE promote_all_to_published
            (pConfirmMsg    OUT VARCHAR2);
    */
        CSMQBean.logger.info("*** CHANGING STATE promote_all_to_published ***");

        String sql = "{call CQT_WHOD_UI_TMS_UTILS.promote_all_to_published(?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        FacesMessage msg;
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "CQT_WHOD_UI_TMS_UTILS.promote_all_to_published");
        String messageText;
        Hashtable retVal = new Hashtable(); // array to return the new state and message

        try {
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.executeUpdate();
            String confirmMsg = cstmt.getString(1);
            if (confirmMsg != null)
                retVal.put("ConfirmMsg", confirmMsg);

           // cstmt.close();
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, confirmMsg, null);
        } catch (SQLException e) {
            e.printStackTrace();

            if (e.getMessage().indexOf(CSMQBean.INVALID_PROMOTION_ERROR) > -1) {
                messageText = CSMQBean.getProperty("INVALID_PROMOTION_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_PROMOTION_SEQUENCE_ERROR) > -1) {
                messageText = CSMQBean.getProperty("INVALID_PROMOTION_SEQUENCE_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.PROMOTION_DEPENDENCY_ERROR) > -1) {
                messageText = CSMQBean.getProperty("PROMOTION_DEPENDENCY_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_STATE_CHANGE_ERROR) > -1) {
                messageText = e.getMessage(); //CSMQBean.getProperty("INVALID_STATE_CHANGE_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.MUST_BE_NMQ_OR_SMQ_ERROR) > -1) {
                messageText = CSMQBean.getProperty("MUST_BE_NMQ_OR_SMQ_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.GENERIC_ACTIVATION_ERROR) > -1) {
                messageText = CSMQBean.getProperty("GENERIC_ACTIVATION_ERROR") + "DETAILS: " + e.getMessage();
            } else if (e.getMessage().indexOf(CSMQBean.DATABASE_CONFIGURATION_ERROR) > -1) {
                messageText = CSMQBean.getProperty("DATABASE_CONFIGURATION_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_STATE_CHANGE_FROM_PENDING_TO_DRAFT_ERROR) > -1) {
                messageText = CSMQBean.getProperty("INVALID_STATE_CHANGE_FROM_PENDING_TO_DRAFT_ERROR");
            } else { // it's something else
                messageText = "The following error occurred: " + e.getMessage();
                e.printStackTrace();
            }
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } finally {
            if (null != cstmt){
                try {
                    cstmt.close();
                }catch (SQLException e){
                    CSMQBean.logger.error("*** Error while closing cstmt ***");
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return retVal;
    }

    public static Hashtable manageWorkflowState(String dictContentIDs, String requestedState, String requestedDesc) {
        /*
    PROCEDURE auto_manage_workflow_state
            (pDictContentID     IN NUMBER,
             pRequestedState    IN VARCHAR2   DEFAULT NULL,
             pRequestedDesc     IN VARCHAR2   DEFAULT NULL)
    */
        CSMQBean.logger.info("*** manageWorkflowState ***");
        CSMQBean.logger.info("dictContentIDs: " + dictContentIDs);

        String sql = "{call CQT_WHOD_UI_TMS_UTILS.auto_manage_workflow_state(?,?,?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        FacesMessage msg;
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "CQT_WHOD_UI_TMS_UTILS.auto_manage_workflow_state");
        String messageText;
        Hashtable retVal = new Hashtable(); // array to return the new state and message

        try {
            cstmt.setString(1, dictContentIDs);
            cstmt.setString(2, requestedState);
            cstmt.setString(3, requestedDesc);
          //  cstmt.executeUpdate();
            cstmt.close();
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success manage Workflow State", null); //TODO need towrite msg
        } catch (SQLException e) {
            e.printStackTrace();

            if (e.getMessage().indexOf(CSMQBean.INVALID_PROMOTION_ERROR) > -1) {
                messageText = CSMQBean.getProperty("INVALID_PROMOTION_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_PROMOTION_SEQUENCE_ERROR) > -1) {
                messageText = CSMQBean.getProperty("INVALID_PROMOTION_SEQUENCE_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.PROMOTION_DEPENDENCY_ERROR) > -1) {
                messageText = CSMQBean.getProperty("PROMOTION_DEPENDENCY_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_STATE_CHANGE_ERROR) > -1) {
                messageText = e.getMessage(); //CSMQBean.getProperty("INVALID_STATE_CHANGE_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.MUST_BE_NMQ_OR_SMQ_ERROR) > -1) {
                messageText = CSMQBean.getProperty("MUST_BE_NMQ_OR_SMQ_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.GENERIC_ACTIVATION_ERROR) > -1) {
                messageText = CSMQBean.getProperty("GENERIC_ACTIVATION_ERROR") + "DETAILS: " + e.getMessage();
            } else if (e.getMessage().indexOf(CSMQBean.DATABASE_CONFIGURATION_ERROR) > -1) {
                messageText = CSMQBean.getProperty("DATABASE_CONFIGURATION_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_STATE_CHANGE_FROM_PENDING_TO_DRAFT_ERROR) > -1) {
                messageText = CSMQBean.getProperty("INVALID_STATE_CHANGE_FROM_PENDING_TO_DRAFT_ERROR");
            } else { // it's something else
                messageText = "The following error occurred: " + e.getMessage();
                e.printStackTrace();
            }
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } finally {
            if (null != cstmt){
                try {
                    cstmt.close();
                }catch (SQLException e){
                    CSMQBean.logger.error("*** Error while closing cstmt ***");
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return retVal;
    }

    public static Hashtable promoteToPublished(String dictContentIDs) {
        /*
    FUNCTION promote_to_published
            (pDictContentID  IN NUMBER,
             pConfirmMsg    OUT VARCHAR2)
          RETURN VARCHAR;
    */
        CSMQBean.logger.info("*** CHANGING STATE FROM promote To Published ***");
        CSMQBean.logger.info("i_dict_content_id: " + dictContentIDs);

        String sql = "{? = call  CQT_WHOD_UI_TMS_UTILS.promote_to_published(?,?)}";
        //String sql = "{ ? = call cqt_whod_ui_tms_utils.copy_all_data(?,?,?,?,?,  ?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        FacesMessage msg;
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "CQT_WHOD_UI_TMS_UTILS.promote_to_published");
        String messageText;
        Hashtable retVal = new Hashtable(); // array to return the new state and message

        try {
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.setString(2, dictContentIDs);
            cstmt.registerOutParameter(3, Types.VARCHAR);
            cstmt.executeUpdate();
            String confirmMsg = cstmt.getString(3);
            if (confirmMsg != null)
                retVal.put("ConfirmMsg", confirmMsg);

           // cstmt.close();
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, confirmMsg, null);
        } catch (SQLException e) {
            e.printStackTrace();

            if (e.getMessage().indexOf(CSMQBean.INVALID_PROMOTION_ERROR) > -1) {
                messageText = CSMQBean.getProperty("INVALID_PROMOTION_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_PROMOTION_SEQUENCE_ERROR) > -1) {
                messageText = CSMQBean.getProperty("INVALID_PROMOTION_SEQUENCE_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.PROMOTION_DEPENDENCY_ERROR) > -1) {
                messageText = CSMQBean.getProperty("PROMOTION_DEPENDENCY_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_STATE_CHANGE_ERROR) > -1) {
                messageText = e.getMessage(); //CSMQBean.getProperty("INVALID_STATE_CHANGE_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.MUST_BE_NMQ_OR_SMQ_ERROR) > -1) {
                messageText = CSMQBean.getProperty("MUST_BE_NMQ_OR_SMQ_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.GENERIC_ACTIVATION_ERROR) > -1) {
                messageText = CSMQBean.getProperty("GENERIC_ACTIVATION_ERROR") + "DETAILS: " + e.getMessage();
            } else if (e.getMessage().indexOf(CSMQBean.DATABASE_CONFIGURATION_ERROR) > -1) {
                messageText = CSMQBean.getProperty("DATABASE_CONFIGURATION_ERROR");
            } else if (e.getMessage().indexOf(CSMQBean.INVALID_STATE_CHANGE_FROM_PENDING_TO_DRAFT_ERROR) > -1) {
                messageText = CSMQBean.getProperty("INVALID_STATE_CHANGE_FROM_PENDING_TO_DRAFT_ERROR");
            } else { // it's something else
                messageText = "The following error occurred: " + e.getMessage();
                e.printStackTrace();
            }
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } finally {
            if (null != cstmt){
                try {
                    cstmt.close();
                }catch (SQLException e){
                    CSMQBean.logger.error("*** Error while closing cstmt ***");
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return retVal;
    }

    public static Hashtable reactivateRetiredTerm(String dictContentIDs) {
        /*
        FUNCTION reactivate_retired_term
                (pDictContentID  IN  NUMBER)
              RETURN VARCHAR2;
    */
        CSMQBean.logger.info("*** CHANGING STATE FROM reactivate_retired_term ***");
        CSMQBean.logger.info("i_dict_content_id: " + dictContentIDs);

        String sql = "{? = call CQT_WHOD_UI_TMS_UTILS.reactivate_retired_term(?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        FacesMessage msg;
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "CQT_WHOD_UI_TMS_UTILS.reactivate_retired_term");
        String messageText;
        Hashtable retVal = new Hashtable(); // array to return the new state and message

        try {
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.setString(2, dictContentIDs);
            cstmt.executeUpdate();
           // cstmt.close();
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Term Ractivated Successfully", null);
        } catch (SQLException e) {
            e.printStackTrace();
            messageText = "The following error occurred: " + e.getMessage();
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } finally {
            if (null != cstmt){
                try {
                    cstmt.close();
                }catch (SQLException e){
                    CSMQBean.logger.error("*** Error while closing cstmt ***");
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return retVal;
    }

    public static Hashtable retireTerm(String dictContentIDs) {
        /*
        FUNCTION retire_term
               (pDictContentID  IN  NUMBER)
             RETURN VARCHAR2;
    */
        CSMQBean.logger.info("*** CHANGING STATE FROM retireTerm ***");
        CSMQBean.logger.info("i_dict_content_id: " + dictContentIDs);

        String sql = "{? = call CQT_WHOD_UI_TMS_UTILS.retire_term(?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        FacesMessage msg;
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "CQT_WHOD_UI_TMS_UTILS.retire_term");
        String messageText;
        Hashtable retVal = new Hashtable(); // array to return the new state and message

        try {
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.setString(2, dictContentIDs);
            cstmt.executeUpdate();
           // cstmt.close();
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Term retired Successfully", null);
        } catch (SQLException e) {
            e.printStackTrace();
            messageText = "The following error occurred: " + e.getMessage();
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageText, "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            return null;
        } finally {
            if (null != cstmt){
                try {
                    cstmt.close();
                }catch (SQLException e){
                    CSMQBean.logger.error("*** Error while closing cstmt ***");
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return retVal;
    }

    public static Hashtable checkActivation() {
        /*
        FUNCTION run_check_activation
                 (pContentErrorCount    OUT NUMBER,
                  pRelationsErrorCount  OUT NUMBER,
                  pInfoNoteErrorCount   OUT NUMBER,
                  pConfirmMsg           OUT VARCHAR2)
              RETURN VARCHAR2
    */
        CSMQBean.logger.info("*** CHANGING STATE FROM checkActivation ***");

        String sql = "{? = call CQT_WHOD_UI_TMS_UTILS.run_check_activation(?,?,?,?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        FacesMessage msg;
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "CQT_WHOD_UI_TMS_UTILS.run_check_activation");
        String messageText;
        Hashtable retVal = new Hashtable(); // array to return the new state and message
        String confirmMsg = null;
        String retVar = null;
        try {
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.registerOutParameter(2, Types.INTEGER);
            cstmt.registerOutParameter(3, Types.INTEGER);
            cstmt.registerOutParameter(4, Types.INTEGER);
            cstmt.registerOutParameter(5, Types.VARCHAR);
            cstmt.executeUpdate();
            int contentErrorCount = cstmt.getInt(2);
            int relationsErrorCount = cstmt.getInt(3);
            int infoNoteErrorCount = cstmt.getInt(4);
            confirmMsg = cstmt.getString(5);
            retVar = cstmt.getString(1);
            //if (contentErrorCount == 0 && relationsErrorCount == 0 && infoNoteErrorCount == 0) {
            if (null != retVar && retVar.equals(RETURN_SUCCESS)) {
                messageText = "The check activation completed successfully for the CDG.";
            } else {
                messageText =
                        "The check activation completed with errors for the CDG; please contact the CQT administrator.";
                CSMQBean.logger.info("*** contentErrorCount ==> " + contentErrorCount);
                CSMQBean.logger.info("*** relationsErrorCount ==> " + relationsErrorCount);
                CSMQBean.logger.info("*** infoNoteErrorCount ==> " + infoNoteErrorCount);
            }
           // cstmt.close();
            CSMQBean.logger.info("*** retVar ==> " + retVar);
            CSMQBean.logger.info("*** confirmMsg ==> " + confirmMsg);
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, messageText, null);
        } catch (SQLException e) {
            e.printStackTrace();
            messageText =
                    "The check activation completed with errors for the CDG; please contact the CQT administrator.";
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, messageText, "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            CSMQBean.logger.error("*** In Exception block  retVar ==> " + retVar);
            CSMQBean.logger.error("*** confirmMsg ==> " + confirmMsg);
            return null;
        } finally {
            if (null != cstmt){
                try {
                    cstmt.close();
                }catch (SQLException e){
                    CSMQBean.logger.error("*** Error while closing cstmt ***");
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return retVal;
    }

    public static Hashtable transferActivation() {
        /*
        FUNCTION run_transfer_activation
                 (pContentErrorCount      OUT NUMBER,
                  pRelationsErrorCount    OUT NUMBER,
                  pInfoNoteErrorCount     OUT NUMBER,
                  pWFTermPublishedCount   OUT NUMBER,
                  pWFTermActivatedCount   OUT NUMBER,
                  pConfirmMsg             OUT VARCHAR2)
              RETURN VARCHAR2
    */
        CSMQBean.logger.info("*** CHANGING STATE FROM transferActivation() ***");

        String sql = "{? = call CQT_WHOD_UI_TMS_UTILS.run_transfer_activation(?,?,?,?,?,?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        FacesMessage msg;
        CallableStatement cstmt =
            new CallableStatement(dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT),
                                  "CQT_WHOD_UI_TMS_UTILS.run_transfer_activation");
        String messageText;
        Hashtable retVal = new Hashtable(); // array to return the new state and message
        String confirmMsg = null;
        String retVar = null;
        try {
            cstmt.registerOutParameter(1, Types.VARCHAR);
            cstmt.registerOutParameter(2, Types.INTEGER);
            cstmt.registerOutParameter(3, Types.INTEGER);
            cstmt.registerOutParameter(4, Types.INTEGER);
            cstmt.registerOutParameter(5, Types.INTEGER);
            cstmt.registerOutParameter(6, Types.INTEGER);
            cstmt.registerOutParameter(7, Types.VARCHAR);
            cstmt.executeUpdate();
            int contentErrorCount = cstmt.getInt(2);
            int relationsErrorCount = cstmt.getInt(3);
            int infoNoteErrorCount = cstmt.getInt(4);
            int wFTermPublishedCount = cstmt.getInt(5);
            int wFTermActivatedCount = cstmt.getInt(6);
            confirmMsg = cstmt.getString(7);
            retVar = cstmt.getString(1);
            CSMQBean.logger.info("*** wFTermPublishedCount ==> " + wFTermPublishedCount);
            CSMQBean.logger.info("*** wFTermActivatedCount ==> " + wFTermActivatedCount);
            CSMQBean.logger.info("*** retVar ==> " + retVar);
            CSMQBean.logger.info("*** confirmMsg ==> " + confirmMsg);
          //  cstmt.close();
           // if (contentErrorCount == 0 && relationsErrorCount == 0 && infoNoteErrorCount == 0
           //     && wFTermPublishedCount == wFTermActivatedCount) {
           if (null != retVar && retVar.equals(RETURN_SUCCESS)) {
                messageText = "The transfer activation completed successfully for the CDG.";
            } else {
                messageText =
                        "The transfer activation completed with errors for the CDG; please contact the CQT administrator.";
            }
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, messageText, null);
        } catch (SQLException e) {
            e.printStackTrace();
            messageText =
                    "The transfer activation completed with errors for the CDG; please contact the CQT administrator.";
            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, messageText, "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
            CSMQBean.logger.error("*** In Exception block  retVar ==> " + retVar);
            CSMQBean.logger.error("*** confirmMsg ==> " + confirmMsg);
            return null;
        } finally {
            if (null != cstmt){
                try {
                    cstmt.close();
                }catch (SQLException e){
                    CSMQBean.logger.error("*** Error while closing cstmt ***");
                }
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        return retVal;
    }

    public static Date getDate(String dateStr, String dateFormat) {
        DateFormat formatter = new SimpleDateFormat(dateFormat);
        java.util.Date date = null;
        try {
            date = formatter.parse(dateStr);
        } catch (java.text.ParseException e) {
        }
        return date;
    }

    public static String getDateStr(Date date, String dateFormat) {
        DateFormat formatter = new SimpleDateFormat(dateFormat);
        String dateStr = null;
        try {
            dateStr = formatter.format(date);
        } catch (Exception e) {
        }
        return dateStr;
    }
}
