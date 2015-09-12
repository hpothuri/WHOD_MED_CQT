package com.dbms.csmq.view.backing.whod;


import com.dbms.csmq.CSMQBean;
import com.dbms.csmq.UserBean;
import com.dbms.csmq.model.infNotes.WHODInfoNotesVORowImpl;
import com.dbms.csmq.view.util.ADFUtils;
import com.dbms.util.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import oracle.adf.controller.ControllerContext;
import oracle.adf.controller.TaskFlowContext;
import oracle.adf.controller.TaskFlowTrainModel;
import oracle.adf.controller.TaskFlowTrainStopModel;
import oracle.adf.controller.ViewPortContext;
import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.uicli.binding.JUCtrlHierNodeBinding;

import org.apache.myfaces.trinidad.event.SelectionEvent;


public class WhodWizardSearchBean {
    public static final String INITIAL_RELEASE_STATUS = "CURRENT";
    private List<SelectItem> whodExtensionSI;
    private List<SelectItem> whodReleaseGroupSI;
    private List<SelectItem> whodProductSI;
    private List<SelectItem> whodGroupSI;
    private List<SelectItem> whodStateSI;
    private List<SelectItem> whodReleaseStatusSI;
    private List<SelectItem> whodLevelGroupSI;

    private String paramExtension = "%";
    private String paramLevelGroup = "%";
    private String paramLevel = CSMQBean.FILTER_LEVEL_ONE;
    private String paramReleaseGroup = CSMQBean.WILDCARD;
    private String paramApproved = CSMQBean.WILDCARD;
    private String paramStatus = CSMQBean.WILDCARD;
    private String paramScope = CSMQBean.WILDCARD;
    private String paramTerm = null;
    private String paramCode = null;
    private String currentRelaseStatus = INITIAL_RELEASE_STATUS;

    private List<String> groupList = new ArrayList<String>();
    private List<String> productList = new ArrayList<String>();
    private List<String> stateList = new ArrayList<String>();
    private String currentStatus;

    private RichTable ctrlSearchResults;
    private RichPopup copyPopup;

    public WhodWizardSearchBean() {
        super();
        loadAllLOVs();
    }

    public void setWhodExtensionSI(List<SelectItem> whodExtensionSI) {
        this.whodExtensionSI = whodExtensionSI;
    }

    public List<SelectItem> getWhodExtensionSI() {
        if (whodExtensionSI == null) {
            whodExtensionSI =
                    ADFUtils.selectItemsForIterator("WHODExtentionListVO1Iterator", "ShortValue", "LongValue");
        }
        return whodExtensionSI;
    }

    public void setWhodReleaseGroupSI(List<SelectItem> whodReleaseGroupSI) {
        this.whodReleaseGroupSI = whodReleaseGroupSI;
    }

    public List<SelectItem> getWhodReleaseGroupSI() {
        if (whodReleaseGroupSI == null) {
            whodReleaseGroupSI = ADFUtils.selectItemsForIterator("WHODGroupList1Iterator", "ShortValue", "LongValue");
        }
        return whodReleaseGroupSI;
    }

    public void setWhodProductSI(List<SelectItem> whodProductSI) {
        this.whodProductSI = whodProductSI;
    }

    public List<SelectItem> getWhodProductSI() {
        if (whodProductSI == null) {
            whodProductSI = ADFUtils.selectItemsForIterator("WHODProductList1Iterator", "ShortValue", "LongValue");
        }
        return whodProductSI;
    }

    public void setWhodGroupSI(List<SelectItem> whodGroupSI) {
        this.whodGroupSI = whodGroupSI;
    }

    public List<SelectItem> getWhodGroupSI() {
        if (whodGroupSI == null) {
            whodGroupSI = ADFUtils.selectItemsForIterator("WHODGroupList1Iterator", "ShortValue", "LongValue");
        }
        return whodGroupSI;
    }

    /**
     * TODO:WHOD Need to check purpose of this method requirement.
     * @param valueChangeEvent
     */
    public void dictionaryChange(ValueChangeEvent valueChangeEvent) {
        if (!valueChangeEvent.getPhaseId().equals(PhaseId.UPDATE_MODEL_VALUES)) {
            valueChangeEvent.setPhaseId(PhaseId.UPDATE_MODEL_VALUES);
            valueChangeEvent.queue();
            return;
        }
        Object currentDictionay = valueChangeEvent.getNewValue();
        if (currentDictionay != null) {
            WhodWizardBean whodWizardBean = WhodUtils.getWhodWizardBean();
            whodWizardBean.setCurrentDictionary(currentDictionay.toString());
            if (CSMQBean.WHOD_BASE_DICTIONARY.equals(whodWizardBean.getCurrentDictionary())) {
                setParamLevelGroup("ATC");
            } else if (CSMQBean.WHOD_FILTER_DICTIONARY.equals(whodWizardBean.getCurrentDictionary())) {
                setParamLevelGroup("CDG");
            }
            setParamLevel("1");
            setWhodLevelGroupSI(null);
            whodWizardBean.setWhodDictinoryLevelsSI(null);
        }
    }

    public void onLevelGroupChange(ValueChangeEvent valueChangeEvent) {
        if (!valueChangeEvent.getPhaseId().equals(PhaseId.UPDATE_MODEL_VALUES)) {
            valueChangeEvent.setPhaseId(PhaseId.UPDATE_MODEL_VALUES);
            valueChangeEvent.queue();
            return;
        }
        Object currentLevelGroup = valueChangeEvent.getNewValue();
        if (currentLevelGroup != null) {
            WhodWizardBean whodWizardBean = WhodUtils.getWhodWizardBean();
            setParamLevel("1");
            whodWizardBean.setWhodDictinoryLevelsSI(null);
        }
    }

    /**
     * TODO:WHOD Need to check purpose of this method requirement.
     * @param valueChangeEvent
     */
    public void extensionChanged(ValueChangeEvent valueChangeEvent) {
        if (!valueChangeEvent.getPhaseId().equals(PhaseId.UPDATE_MODEL_VALUES)) {
            valueChangeEvent.setPhaseId(PhaseId.UPDATE_MODEL_VALUES);
            valueChangeEvent.queue();
            return;
        }
    }

    /**
     * TODO:WHOD Need to check purpose of this method requirement.
     * @param valueChangeEvent
     */
    public void releaseStatusChanged(ValueChangeEvent valueChangeEvent) {
        String currentRelaseStatus = valueChangeEvent.getNewValue().toString();
        this.setCurrentRelaseStatus(currentRelaseStatus);
        getWhodStateSI();
    }

    public void setFocusToSelectedRowBack() {
        DCBindingContainer bindings = (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding parentIter = (DCIteratorBinding)bindings.get("WHODSimpleSearch1Iterator");
        if ((parentIter != null) && (parentIter.getCurrentRow() != null)) {
            Key parentKey = parentIter.getCurrentRow().getKey();
            parentIter.setCurrentRowWithKey(parentKey.toStringFormat(true));
        }
    }

    public void doSearch(ActionEvent actionEvent) {
        UserBean userBean = WhodUtils.getUserBean();
        DCIteratorBinding dciterb = ADFUtils.findIterator("WHODSimpleSearch1Iterator");
        ViewObject vo = dciterb.getViewObject();
        WhodWizardBean whodWizardBean = WhodUtils.getWhodWizardBean();
        vo.setNamedWhereClauseParam("approvedFlag", getParamApproved());
        vo.setNamedWhereClauseParam("levelName", getSearchLevelParam());
        if(getParamExtension() != null && getParamExtension().equalsIgnoreCase("CDG"))
        vo.setNamedWhereClauseParam("dGActiveStatus", getParamStatus());
        else if(getParamLevelGroup() != null && getParamLevelGroup().equals("CDG"))
        vo.setNamedWhereClauseParam("dGActiveStatus", getParamStatus());   
        else
        vo.setNamedWhereClauseParam("dGActiveStatus", "%");
        vo.setNamedWhereClauseParam("dGGroupLIST", getParamGroupList()); // search needs ^ as the delimiter
        vo.setNamedWhereClauseParam("dGProductLIST", getParamProductList()); // search needs ^ as the delimiter
        vo.setNamedWhereClauseParam("dGScopeFlag", getParamScope());
        vo.setNamedWhereClauseParam("dReleaseStatus", getCurrentRelaseStatus());
        if (whodWizardBean != null && whodWizardBean.getCurrentDictionary().equals("UMCSDG2"))
            vo.setNamedWhereClauseParam("dictionary_type", "FILTER");
        else
            vo.setNamedWhereClauseParam("dictionary_type", "BASE");

        String paramTermVal = getParamTerm();
        if (null != paramTermVal && !paramTermVal.isEmpty()) {
            paramTermVal = paramTermVal.replace("'", "\''");
            vo.setNamedWhereClauseParam("termUpper", paramTermVal + "%");
        } else {
            vo.setNamedWhereClauseParam("termUpper", "%");
        }

        vo.setNamedWhereClauseParam("dictContentCode", (getParamCode() != null ? getParamCode() : "") + "%");
        vo.executeQuery();
        //CLEAR OLD TRESS
        //TODO need to check why we need this
        //WhodUtils.clearRelations();
        if (ctrlSearchResults != null) { // if we are calling this from IA, we won't need this
            ctrlSearchResults.setEmptyText("No data to display.");
            AdfFacesContext.getCurrentInstance().addPartialTarget(ctrlSearchResults);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(ctrlSearchResults);
        }
        //clear the selected row
        ctrlSearchResults.getSelectedRowKeys().clear();
        AdfFacesContext.getCurrentInstance().addPartialTarget(ctrlSearchResults);
    }

    private String getSearchLevelParam() {
        Object setMode = ADFContext.getCurrent().getPageFlowScope().get("setMode");
        if (setMode != null && setMode.toString().equals("update")) {
            if ((getParamExtension() != null && getParamExtension().equals("TDG")))
                return "TDG" + getParamLevel() + "%";
            else
                return "CDG" + getParamLevel() + "%";
        } else {
            if (getParamLevelGroup() == null || getParamLevelGroup().equals("")) {
                return "%";
            } else
                return getParamLevelGroup() + getParamLevel() + "%";
        }
    }

    public void onTableNodeSelection(SelectionEvent selectionEvent) {
        UserBean userBean = WhodUtils.getUserBean();
        WhodWizardBean whodWizardBean = WhodUtils.getWhodWizardBean();
        CSMQBean.logger.info(userBean.getCaller() + " ***** ROW CHANGE START ****");
        whodWizardBean.setTreeAccessed(false); //reset this to recreate the tree when the page loads
        whodWizardBean.clearDetails(); // hopefully this works
        ADFUtils.resolveMethodExpression("#{bindings.WHODSimpleSearch1.collectionModel.makeCurrent}", null,
                                         new Class[] { SelectionEvent.class }, new Object[] { selectionEvent });
        RichTable object = (RichTable)selectionEvent.getSource();
        Row row = null;
        for (Object facesRowKey : object.getSelectedRowKeys()) {
            object.setRowKey(facesRowKey);
            Object o = object.getRowData();
            JUCtrlHierNodeBinding rowData = (JUCtrlHierNodeBinding)o;
            if (rowData == null)
                return;
            row = rowData.getRow();
        }

        if (row == null)
            return;

        processSearchResults(row);
        // get the notes. TODO:WHOD -- Moved this method to processSearchResults()
        //getInfNotes(currentState, currentStatus, currentMqcode, currentDictionary, currentDictContentID);

        CSMQBean.logger.info(userBean.getCaller() + " ***** ROW CHANGE COMPLETE ****");
    }

    private void processSearchResults(Row row) {
        UserBean userBean = WhodUtils.getUserBean();
        String currentTermName = Utils.getAsString(row, "Term");
        CSMQBean.logger.info(userBean.getCaller() + " currentTermName:" + currentTermName);
        String currentMqcode = Utils.getAsString(row, "DictContentCode");
        CSMQBean.logger.info(userBean.getCaller() + " currentMqcode:" + currentMqcode);
        //String currentDictionary = "CQTSDG";
        String currentDictionary = Utils.getAsString(row, "DictionaryName");
        //CSMQBean.logger.info(userBean.getCaller() + " currentDictionary:" + currentDictionary);
        String currentReleaseGroup = Utils.getAsString(row, "ReleaseType");
        CSMQBean.logger.info(userBean.getCaller() + " currentReleaseGroup:" + currentReleaseGroup);
        String currentMqstatus = Utils.getAsString(row, "Value1");
        CSMQBean.logger.info(userBean.getCaller() + " currentMqstatus:" + currentMqstatus);
        String currentDictContentID = Utils.getAsString(row, "DictContentId");
        CSMQBean.logger.info(userBean.getCaller() + " currentDictContentID:" + currentDictContentID);
        String currentApprovalFlag = Utils.getAsString(row, "ApprovedFlag");
        CSMQBean.logger.info(userBean.getCaller() + " currentApprovalFlag:" + currentApprovalFlag);
        String currentVersion = Utils.getAsString(row, "DictionaryVersion");
        CSMQBean.logger.info(userBean.getCaller() + " currentVersion:" + currentVersion);
        String currentSubType = Utils.getAsString(row, "TermSubtype");
        CSMQBean.logger.info(userBean.getCaller() + " currentSubType:" + currentSubType);
        String currentCreatedBy = Utils.getAsString(row, "CreatedBy");
        CSMQBean.logger.info(userBean.getCaller() + " Createdby:" + currentCreatedBy);
        // currentExtension = Utils.getAsString(row, "Extension");

        String currentState = Utils.getAsString(row, "State");
        CSMQBean.logger.info(userBean.getCaller() + " currentState:" + currentState);
        String currentStatus = Utils.getAsString(row, "ReleaseStatus");
        CSMQBean.logger.info(userBean.getCaller() + " currentStatus:" + currentStatus);
        String levelName = Utils.getAsString(row, "LevelName");
        CSMQBean.logger.info(userBean.getCaller() + " CurrentTermLevel:" + levelName);
        // System.out.println("--------"+levelName);
        String currentlevelumber = levelName.substring(3, 4);
        String currentExtension = levelName.substring(0, 3);
        CSMQBean.logger.info(userBean.getCaller() + " Extension:" + currentExtension);
        String currentMqproduct = Utils.getAsString(row, "Value3");
        CSMQBean.logger.info(userBean.getCaller() + " Product:" + currentMqproduct);

        String currentMqgroups = Utils.getAsString(row, "Value4");
        CSMQBean.logger.info(userBean.getCaller() + " Group:" + currentMqgroups);
        String currentDesignees = Utils.getAsString(row, "Designee");
        CSMQBean.logger.info(userBean.getCaller() + " Designee:" + currentDesignees);

        WhodWizardBean whodWizardBean = WhodUtils.getWhodWizardBean();
        whodWizardBean.setCurrentExtension(currentExtension);
        whodWizardBean.setCurrentTermName(currentTermName);
        whodWizardBean.setCurrentFilterDictionaryShortName(currentDictionary);
        whodWizardBean.setCurrentPredictGroups(currentReleaseGroup); //<--test
        whodWizardBean.setCurrentTermLevel(currentlevelumber); //
        whodWizardBean.setCurrentContentCode(currentMqcode);
        whodWizardBean.setCurrentDictContentID(currentDictContentID);
        whodWizardBean.setActiveDictionary(currentDictionary);
        whodWizardBean.setCurrentMQStatus(currentMqstatus);
        whodWizardBean.setCurrentVersion(currentVersion);
        whodWizardBean.setCurrentCreatedBy(currentCreatedBy);
        whodWizardBean.setCurrentExtension(currentExtension);
        if(whodWizardBean.getMode() == CSMQBean.MODE_COPY_EXISTING){
        whodWizardBean.setCurrentStatus("PENDING");   
        }else{
        whodWizardBean.setCurrentStatus(currentStatus);
        }
        whodWizardBean.setCurrentProduct(currentMqproduct);
        whodWizardBean.setCurrentMQGROUP(currentMqgroups);
        setParamLevelGroup(currentExtension);
        whodWizardBean.setCurrentState(currentState);

        /*
         * //TODO:WHOD need to verify - What are all the correct values.
        whodWizardBean.setCurrentScope(currentMqscp);
        whodWizardBean.setIsApproved(isApproved);
        whodWizardBean.setCurrentState(currentState);
        whodWizardBean.setCurrentMQCRTEV(currentCriticalEvent);
        whodWizardBean.setCurrentMQALGO(currentMqalgo);
        whodWizardBean.setCurrentMQGROUP(currentMqgroups);
        whodWizardBean.setCurrentProduct(currentMqproduct);
        whodWizardBean.setCurrentDateRequested(currentDateRequested);
        whodWizardBean.setCurrentRequestedByDate(requestedByDate);
        whodWizardBean.setCurrentReasonForRequest(currentReasonForRequest);
        whodWizardBean.setCurrentReasonForApproval(currentReasonForApproval);
        whodWizardBean.setIsApproved(isApproved);
        whodWizardBean.setCurrentCutOffDate(currentCutOffDate);
        whodWizardBean.setCurrentCreateDate(currentCreateDate);
        whodWizardBean.setCurrentUntilDate(currentUntilDate);
        */

        /**
         * TODO:WHOD --> Check why we need this call.
         */
        whodWizardBean.getDictionaryInfo(); // GET BASE DICT INFO FROM FILTER

        Hashtable<String, String> activationInfo =
            WhodUtils.getActivationInfo(currentDictContentID, currentDictionary);

        if (activationInfo != null) {
            whodWizardBean.setCurrentInitialCreationDate(activationInfo.get("initialCreationDate"));
            whodWizardBean.setCurrentInitialCreationBy(activationInfo.get("initialCreationBy"));
            whodWizardBean.setCurrentLastActivationDate(activationInfo.get("lastActivationDate"));
            whodWizardBean.setCurrentActivationBy(activationInfo.get("activationBy"));

            CSMQBean.logger.info(userBean.getCaller() + " initialCreationDate:" +
                                 activationInfo.get("initialCreationDate"));
            CSMQBean.logger.info(userBean.getCaller() + " initialCreationBy:" +
                                 activationInfo.get("initialCreationBy"));
            CSMQBean.logger.info(userBean.getCaller() + " lastActivationDate:" +
                                 activationInfo.get("lastActivationDate"));
            CSMQBean.logger.info(userBean.getCaller() + " activationBy:" + activationInfo.get("activationBy"));
        } else {
            CSMQBean.logger.info(userBean.getCaller() + " *** UNABLE TO GET ACTIVATION INFO *** ");
            whodWizardBean.setCurrentInitialCreationDate("N/A");
            whodWizardBean.setCurrentInitialCreationBy("N/A");
            whodWizardBean.setCurrentLastActivationDate("N/A");
            whodWizardBean.setCurrentActivationBy("N/A");
        }

        /**
         * TODO:WHOD --> Check why we need this call.
         */
        //        if (whodWizardBean.getMode() != CSMQBean.MODE_COPY_EXISTING) {
        //            whodWizardBean.setDesigneeList(getDesignees(currentDictContentID));
        //        }


        if (currentMqproduct != null) {
            currentMqproduct = currentMqproduct.replace(CSMQBean.DEFAULT_DELIMETER_CHAR, '%');
            Collections.addAll(whodWizardBean.getProductList(), currentMqproduct.split("%"));
        }

        if (currentMqgroups != null) {
            currentMqgroups = currentMqgroups.replace(CSMQBean.DEFAULT_DELIMETER_CHAR, '%');
            Collections.addAll(whodWizardBean.getMQGroupList(), currentMqgroups.split("%"));
        }

        if (currentDesignees != null) {
            currentDesignees = currentDesignees.replace(CSMQBean.DEFAULT_DELIMETER_CHAR, '%');
            Collections.addAll(whodWizardBean.getDesigneeList(), currentDesignees.split("%"));
        }

        // get the notes.
        getInfNotes(currentState, currentStatus, currentMqcode, currentDictionary, currentDictContentID);
    }

    public void getInfNotes(String currentState, String currentStatus, String currentMqcode, String currentDictionary,
                            String currentDictContentID) {
        try {
            CSMQBean cSMQBean = WhodUtils.getCSMQBean();
            UserBean userBean = WhodUtils.getUserBean();
            DCIteratorBinding dciterb = ADFUtils.findIterator("WHODInfoNotesIterator");
            ViewObject vo = dciterb.getViewObject();
            //currentDictContentID = "918101681";

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

    public void getInfNotes1(String currentState, String currentStatus, String currentMqcode, String currentDictionary,
                             String currentDictContentID) {
        try {
            CSMQBean cSMQBean = WhodUtils.getCSMQBean();
            UserBean userBean = WhodUtils.getUserBean();
            DCIteratorBinding dciterb = ADFUtils.findIterator("InfNotesVO1Iterator");
            ViewObject vo = dciterb.getViewObject();
            String relGroup = cSMQBean.getDefaultDraftReleaseGroup();
            if (currentState != null && currentState.equals(CSMQBean.STATE_PUBLISHED))
                relGroup = CSMQBean.defaultPublishReleaseGroup;
            if (currentState != null && currentState.equals(CSMQBean.IA_STATE_PUBLISHED))
                relGroup = CSMQBean.defaultMedDRAReleaseGroup;

            String tStatus = CSMQBean.CURRENT_IF_PENDING_NULL;
            if (currentStatus != null && currentStatus.equals(CSMQBean.CURRENT_RELEASE_STATUS))
                tStatus = CSMQBean.CURRENT_RELEASE_STATUS;

            CSMQBean.logger.info(userBean.getCaller() + " ** GETTING INF NOTES **");
            CSMQBean.logger.info(userBean.getCaller() + " dictContentCode: " + currentMqcode);
            CSMQBean.logger.info(userBean.getCaller() + " dictShortName: " + currentDictionary);
            CSMQBean.logger.info(userBean.getCaller() + " SMQNOTE: " +
                                 CSMQBean.getProperty("SMQ_NOTE_INFORMATIVE_NOTE"));
            CSMQBean.logger.info(userBean.getCaller() + " SMQDESC: " +
                                 CSMQBean.getProperty("SMQ_DESCRIPTION_INFORMATIVE_NOTE"));
            CSMQBean.logger.info(userBean.getCaller() + " SMQSRC: " +
                                 CSMQBean.getProperty("SMQ_SOURCE_INFORMATIVE_NOTE"));
            CSMQBean.logger.info(userBean.getCaller() + " groupname: " + relGroup);
            CSMQBean.logger.info(userBean.getCaller() + " dictContentID: " + currentDictContentID);
            CSMQBean.logger.info(userBean.getCaller() + " currentPendingStatus: " + tStatus);

            vo.setNamedWhereClauseParam("dictContentCode", currentMqcode);
            vo.setNamedWhereClauseParam("dictShortName", currentDictionary);
            vo.setNamedWhereClauseParam("SMQNOTE", CSMQBean.getProperty("SMQ_NOTE_INFORMATIVE_NOTE"));
            vo.setNamedWhereClauseParam("SMQDESC", CSMQBean.getProperty("SMQ_DESCRIPTION_INFORMATIVE_NOTE"));
            vo.setNamedWhereClauseParam("SMQSRC", CSMQBean.getProperty("SMQ_SOURCE_INFORMATIVE_NOTE"));
            vo.setNamedWhereClauseParam("groupname", relGroup);
            vo.setNamedWhereClauseParam("dictContentID", currentDictContentID);
            vo.setNamedWhereClauseParam("currentPendingStatus", tStatus);
            vo.executeQuery();
            Enumeration rows = dciterb.getRowSetIterator().enumerateRowsInRange();
            if (!rows.hasMoreElements()) {
                CSMQBean.logger.info(userBean.getCaller() + " ! THERE ARE NO INF NOTES !");
                return;
            }
            Row row = (Row)rows.nextElement();

            WhodWizardBean whodWizardBean = WhodUtils.getWhodWizardBean();
            if (whodWizardBean.getMode() == CSMQBean.MODE_IMPACT_ASSESSMENT)
                tStatus = CSMQBean.CURRENT_IF_PENDING_NULL_IA;
            whodWizardBean.setCurrentInfNoteDescription(Utils.getAsString(row, "Mqdesc"));
            whodWizardBean.setCurrentInfNoteNotes(Utils.getAsString(row, "Mqnote"));
            whodWizardBean.setCurrentInfNoteSource(Utils.getAsString(row, "Mqsrc"));
        } catch (java.util.NoSuchElementException e) {
            CSMQBean.logger.error(e.getMessage(), e);
        }
    }

    public void setParamExtension(String paramExtension) {
        this.paramExtension = paramExtension;
    }

    public String getParamExtension() {
        return paramExtension;
    }

    public void setParamLevel(String paramLevel) {
        this.paramLevel = paramLevel;
    }

    public String getParamLevel() {
        return paramLevel;
    }

    public void setParamReleaseGroup(String paramReleaseGroup) {
        this.paramReleaseGroup = paramReleaseGroup;
    }

    public String getParamReleaseGroup() {
        return paramReleaseGroup;
    }

    public void setParamApproved(String paramApproved) {
        this.paramApproved = paramApproved;
    }

    public String getParamApproved() {
        return paramApproved;
    }

    public void setGroupList(List<String> groupList) {
        this.groupList = groupList;
    }

    public List<String> getGroupList() {
        return groupList;
    }

    public void setProductList(List<String> productList) {
        this.productList = productList;
    }

    public List<String> getProductList() {
        return productList;
    }

    public void setParamStatus(String paramStatus) {
        this.paramStatus = paramStatus;
    }

    public String getParamStatus() {
        return paramStatus;
    }

    public void setStateList(List<String> stateList) {
        this.stateList = stateList;
    }

    public List<String> getStateList() {
        return stateList;
    }

    public void setParamScope(String paramScope) {
        this.paramScope = paramScope;
    }

    public String getParamScope() {
        return paramScope;
    }

    public void setParamTerm(String paramTerm) {
        this.paramTerm = paramTerm;
    }

    public String getParamTerm() {
        return paramTerm;
    }

    public void setParamCode(String paramCode) {
        this.paramCode = paramCode;
    }

    public String getParamCode() {
        return paramCode;
    }

    public String getParamGroupList() {
        return formSearchStringFromStrList(getGroupList());
    }

    public String getParamProductList() {
        return formSearchStringFromStrList(getProductList());
    }

    private String formSearchStringFromStrList(List<String> selected) {
        int size = 0;
        if (selected == null || selected.size() == 0) {
            return CSMQBean.WILDCARD;
        }

        String paramGroupList = CSMQBean.WILDCARD;
        String temp = "";
        size = selected.size();
        for (Object s : selected)
            temp = temp + s + CSMQBean.DEFAULT_DELIMETER_CHAR;

        temp.replace("[", "");
        temp.replace("]", "");

        if (temp != null & temp.length() > 0)
            paramGroupList = temp.substring(0, temp.length() - 1);
        if (size == 1)
            paramGroupList = "%" + paramGroupList + "%";

        return paramGroupList.replace(CSMQBean.DEFAULT_DELIMETER_CHAR, CSMQBean.DEFAULT_DELIMETER_CHAR);
    }

    /**
     * TODO:WHOD Find out why we need this method
     * @param mqCode
     * @param dictContentID
     * @param releaseGroups
     */
    public void initForImpactAnalysis(String mqCode, String dictContentID, String releaseGroups) {
        // used for impact analysis
        //        this.IASearch = true;
        //        this.currentMqcode = mqCode;
        //        this.paramMQCode = mqCode;
        //        this.paramQueryType = CSMQBean.SMQ_SEARCH;
        //        this.currentDictContentID = dictContentID;
        //        this.paramState = CSMQBean.WILDCARD;
        //        this.currentDictionary = CSMQBean.defaultFilterDictionaryShortName;
        //        this.paramDictName = CSMQBean.defaultFilterDictionaryShortName;
        //        this.paramReleaseGroup = releaseGroups;
        //        //setUIDefaults();
        //
        //        doSearch(null); // run the search with the params above - these come from the IA search
        //        //this.getInfNotes();
        //        // get the results - there should just be one row
        //        BindingContext bc = BindingContext.getCurrent();
        //        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        //        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get(searchIterator);
        //
        //        Enumeration rows = dciterb.getRowSetIterator().enumerateRowsInRange();
        //        if (rows == null || !rows.hasMoreElements())
        //            return;
        //
        //        Row row = (Row)rows.nextElement();
        //
        //
        //        processSearchResults(row);
        //        //nMQWizardBean.setMode(mode);
        //        nMQWizardBean.setCurrentTermName(currentTermName);
        //        nMQWizardBean.setCurrentFilterDictionaryShortName(this.currentDictionary);
        //        nMQWizardBean.getDictionaryInfo(); // GET BASE DICT INFO FROM FILTER
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCtrlSearchResults(RichTable ctrlSearchResults) {
        this.ctrlSearchResults = ctrlSearchResults;
    }

    public RichTable getCtrlSearchResults() {
        return ctrlSearchResults;
    }

    public void setWhodStateSI(List<SelectItem> whodStateSI) {
        this.whodStateSI = whodStateSI;
    }

    public List<SelectItem> getWhodStateSI() {
        // if (whodStateSI == null) {
        UserBean userBean = WhodUtils.getUserBean();
        List<SelectItem> selectItems = new ArrayList<SelectItem>();
        List<SelectItem> selectItemsBeforeLogin = new ArrayList<SelectItem>();
        selectItems =
                ADFUtils.selectItemsForIteratorbyPageDef(WhodWizardBean.WHOD_SEARCH_PAGE_DEF, "WHODStateListVO1Iterator",
                                                         "ShortValue", "ShortValue");
        if (userBean.isLoggedIn() && !this.getCurrentRelaseStatus().equals("CURRENT"))
            whodStateSI = selectItems;
        else {
            for (SelectItem selectItem : selectItems) {
                if (selectItem.getValue() != null && selectItem.getValue().toString().equals("ACTIVATED")) {
                    selectItemsBeforeLogin.add(selectItem);
                }
                whodStateSI = selectItemsBeforeLogin;
            }
        }
        // }
        return whodStateSI;
    }

    public void setWhodReleaseStatusSI(List<SelectItem> whodReleaseStatusSI) {
        this.whodReleaseStatusSI = whodReleaseStatusSI;
    }

    public List<SelectItem> getWhodReleaseStatusSI() {
        if (whodReleaseStatusSI == null) {
            UserBean userBean = WhodUtils.getUserBean();
            List<SelectItem> selectItems = new ArrayList<SelectItem>();
            List<SelectItem> selectItemsBeforeLogin = new ArrayList<SelectItem>();
            selectItems =
                    ADFUtils.selectItemsForIteratorbyPageDef(WhodWizardBean.WHOD_SEARCH_PAGE_DEF, "WHODReleaseStatuListVO1Iterator",
                                                             "ShortValue", "LongValue");
            if (userBean.isLoggedIn())
                whodReleaseStatusSI = selectItems;
            else {
                for (SelectItem selectItem : selectItems) {
                    if (selectItem.getValue() != null && selectItem.getValue().toString().equals("CURRENT")) {
                        selectItemsBeforeLogin.add(selectItem);
                    }
                    whodReleaseStatusSI = selectItemsBeforeLogin;
                }
            }
        }
        return whodReleaseStatusSI;
    }

    public void setCurrentRelaseStatus(String currentRelaseStatus) {
        this.currentRelaseStatus = currentRelaseStatus;
    }

    public String getCurrentRelaseStatus() {
        return currentRelaseStatus;
    }

    public void loadAllLOVs() {
        try {
            getWhodStateSI();
            getWhodReleaseStatusSI();
            String mode = (String)ADFContext.getCurrent().getPageFlowScope().get("setMode");
            if (!"update".equals(mode) && !"create".equals(mode)) {
                getWhodLevelGroupSI();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setWhodLevelGroupSI(List<SelectItem> whodLevelGroupSI) {
        this.whodLevelGroupSI = whodLevelGroupSI;
    }

    public List<SelectItem> getWhodLevelGroupSI() {
        if (whodLevelGroupSI == null) {
            List<SelectItem> selectItems = new ArrayList<SelectItem>();
            List<SelectItem> selectItemsToSet = new ArrayList<SelectItem>();
            selectItems = ADFUtils.selectItemsForIterator("WHODGroupLevelVO1Iterator", "ShortValue", "ShortValue");
            WhodWizardBean whodWizardBean = WhodUtils.getWhodWizardBean();
            if (whodWizardBean != null &&
                whodWizardBean.getCurrentDictionary().equals(CSMQBean.WHOD_BASE_DICTIONARY)) {
                for (SelectItem selectItem : selectItems) {
                    if (selectItem.getValue().toString().equals("ATC"))
                        selectItemsToSet.add(selectItem);
                }
                whodLevelGroupSI = selectItemsToSet;
            } else {
                for (SelectItem selectItem : selectItems) {
                    if (selectItem.getValue().toString().equals("CDG") ||
                        selectItem.getValue().toString().equals("SDG"))
                        selectItemsToSet.add(selectItem);
                }
                whodLevelGroupSI = selectItemsToSet;
            }
        }
        return whodLevelGroupSI;
    }

    public void setParamLevelGroup(String paramLevelGroup) {
        this.paramLevelGroup = paramLevelGroup;
    }

    public String getParamLevelGroup() {
        return paramLevelGroup;
    }
    
    public void openPopup(ActionEvent actionEvent) {
        Iterator  itr = ctrlSearchResults.getSelectedRowKeys().iterator();
        boolean isRowSeleted = itr.hasNext();
        System.out.println("---"+itr.hasNext());
        WhodWizardBean whodWizardBean = WhodUtils.getWhodWizardBean();
        if(isRowSeleted){
        if (whodWizardBean.getMode() == CSMQBean.MODE_COPY_EXISTING) {
            RichPopup.PopupHints hints = new RichPopup.PopupHints();
            copyPopup.show(hints);
            //}
        }
        }else{
            FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Please select Row to copy", "");
            FacesContext.getCurrentInstance().addMessage(null, msg);
        }
    }

    public void setCopyPopup(RichPopup copyPopup) {
        this.copyPopup = copyPopup;
    }

    public RichPopup getCopyPopup() {
        return copyPopup;
    }
    
    public String saveCopiedDetails() {
        
        WhodWizardBean whodWizardBean = WhodUtils.getWhodWizardBean();
        boolean retVal = whodWizardBean.copyAndSaveDetails();
       // queueTrainStopEventToRegion("#{viewScope.TrainHandlerBeanHelper.getSelectedTrainStopOutcome}",actionEvent.getComponent());
       String nextStopAction = null;
        if(retVal){
        ControllerContext controllerContext = ControllerContext.getInstance();
        ViewPortContext currentViewPortCtx = controllerContext.getCurrentViewPort();
        TaskFlowContext taskFlowCtx = currentViewPortCtx.getTaskFlowContext();
        TaskFlowTrainModel taskFlowTrainModel = taskFlowCtx.getTaskFlowTrainModel();
        TaskFlowTrainStopModel currentStop = taskFlowTrainModel.getCurrentStop();
        TaskFlowTrainStopModel nextStop = taskFlowTrainModel.getNextStop(currentStop);
        nextStopAction = nextStop.getOutcome();

           whodWizardBean.setOpenPopup(true);
            copyPopup.hide();
        return nextStopAction;
        }else{
            return nextStopAction;
        }
    }
}
