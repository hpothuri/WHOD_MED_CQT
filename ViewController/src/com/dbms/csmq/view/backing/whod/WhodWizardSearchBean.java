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
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.uicli.binding.JUCtrlHierNodeBinding;

import org.apache.myfaces.trinidad.event.SelectionEvent;


public class WhodWizardSearchBean {
    private List<SelectItem> whodExtensionSI;
    private List<SelectItem> whodReleaseGroupSI;
    private List<SelectItem> whodProductSI;
    private List<SelectItem> whodGroupSI;

    private String paramExtension = "%";
    private String paramLevel = CSMQBean.FILTER_LEVEL_ONE;
    private String paramReleaseGroup = CSMQBean.WILDCARD;
    private String paramApproved = CSMQBean.WILDCARD;
    private String paramStatus = CSMQBean.WILDCARD;
    private String paramScope = CSMQBean.WILDCARD;
    private String paramTerm = null;
    private String paramCode = null;

    private List<String> groupList = new ArrayList<String>();
    private List<String> productList = new ArrayList<String>();
    private List<String> stateList = new ArrayList<String>();
    private String currentStatus;

    private RichTable ctrlSearchResults;

    public WhodWizardSearchBean() {
        super();
    }

    public void setWhodExtensionSI(List<SelectItem> whodExtensionSI) {
        this.whodExtensionSI = whodExtensionSI;
    }

    public List<SelectItem> getWhodExtensionSI() {
        if (whodExtensionSI == null) {
            whodExtensionSI =
                    ADFUtils.selectItemsForIterator("WHODExtentionListVO1Iterator", "ShortValue",
                                                    "LongValue");
        }
        return whodExtensionSI;
    }

    public void setWhodReleaseGroupSI(List<SelectItem> whodReleaseGroupSI) {
        this.whodReleaseGroupSI = whodReleaseGroupSI;
    }

    public List<SelectItem> getWhodReleaseGroupSI() {
        if (whodReleaseGroupSI == null) {
            whodReleaseGroupSI = ADFUtils.selectItemsForIterator("WHODGroupList1Iterator", "ShortVal", "LongValue");
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
    }

    /**
     * TODO:WHOD Need to check purpose of this method requirement.
     * @param valueChangeEvent
     */
    public void extensionChanged(ValueChangeEvent valueChangeEvent) {
    }

    /**
     * TODO:WHOD Need to check purpose of this method requirement.
     * @param valueChangeEvent
     */
    public void releaseStatusChanged(ValueChangeEvent valueChangeEvent) {
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

        vo.setNamedWhereClauseParam("approvedFlag", getParamApproved());
        vo.setNamedWhereClauseParam("levelName", getSearchLevelParam());
        vo.setNamedWhereClauseParam("dGActiveStatus", getParamStatus());
        vo.setNamedWhereClauseParam("dGGroupLIST", getParamGroupList()); // search needs ^ as the delimiter
        vo.setNamedWhereClauseParam("dGProductLIST", getParamProductList()); // search needs ^ as the delimiter
        vo.setNamedWhereClauseParam("dGScopeFlag", getParamScope());

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
        return getParamExtension() + getParamLevel() + "%";
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
        String currentExtension = levelName.substring(0, 3);    
        CSMQBean.logger.info(userBean.getCaller() + " Extension:" + currentExtension);
        
        String currentMqproduct = Utils.getAsString(row, "Value3");
        CSMQBean.logger.info(userBean.getCaller() + " Product:" + currentMqproduct);
        
        String currentMqgroups = Utils.getAsString(row, "Value4");
        CSMQBean.logger.info(userBean.getCaller() + " Group:" + currentMqgroups);
        
        WhodWizardBean whodWizardBean = WhodUtils.getWhodWizardBean();
        whodWizardBean.setCurrentExtension(currentExtension);
        whodWizardBean.setCurrentTermName(currentTermName);
        whodWizardBean.setCurrentFilterDictionaryShortName(currentDictionary);
        whodWizardBean.setCurrentPredictGroups(currentReleaseGroup); //<--test
        whodWizardBean.setCurrentTermLevel(levelName); //
        whodWizardBean.setCurrentContentCode(currentMqcode);
        whodWizardBean.setCurrentDictContentID(currentDictContentID);
        whodWizardBean.setActiveDictionary(currentDictionary);
        whodWizardBean.setCurrentMQStatus(currentMqstatus);
        whodWizardBean.setCurrentVersion(currentVersion);
        whodWizardBean.setCurrentCreatedBy(currentCreatedBy);
        whodWizardBean.setCurrentExtension(currentExtension);
        whodWizardBean.setCurrentStatus(currentStatus);
        whodWizardBean.setCurrentProduct(currentMqproduct);
        whodWizardBean.setCurrentMQGROUP(currentMqgroups);

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

        //        Hashtable<String, String> activationInfo =
        //            WhodUtils.getActivationInfo(currentDictContentID, currentDictionary);
        Hashtable<String, String> activationInfo = null;

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

        //whodWizardBean.getProductList().clear();
        //whodWizardBean.getMQGroupList().clear();
        //whodWizardBean.getDesigneeList().clear();
        //Collections.addAll(whodWizardBean.getProductList(), new String[] { "AIN457", "ABILIFY" });
        //Collections.addAll(whodWizardBean.getMQGroupList(), new String[] { "APO", "AAA" });
        whodWizardBean.setDesigneeList(new ArrayList<String>());
        //Collections.addAll(whodWizardBean.getDesigneeList(), new String[] { "CQT_ML", "OPS$TEST91" });

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
            if(vo.getRowCount() > 0){
                while(vo.hasNext()){
                WHODInfoNotesVORowImpl row = (WHODInfoNotesVORowImpl)vo.next();
                if(row.getInfoNoteType() != null && row.getInfoNoteType().equals("DESC"))
                whodWizardBean.setCurrentInfNoteDescription(Utils.getAsString(row, "InfoNoteValue"));
                if(row.getInfoNoteType() != null && row.getInfoNoteType().equals("NOTE"))
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
        if (selected == null || selected.size() == 0) {
            return CSMQBean.WILDCARD;
        }

        String paramGroupList = CSMQBean.WILDCARD;
        String temp = "";
        for (Object s : selected)
            temp = temp + s + CSMQBean.DEFAULT_DELIMETER_CHAR;

        temp.replace("[", "");
        temp.replace("]", "");

        if (temp != null & temp.length() > 0)
            paramGroupList = temp.substring(0, temp.length() - 1);

        return paramGroupList.replace(CSMQBean.DEFAULT_DELIMETER_CHAR, CSMQBean.DEFAULT_SEARCH_DELIMETER_CHAR);
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
}
