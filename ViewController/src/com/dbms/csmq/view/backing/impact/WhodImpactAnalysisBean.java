package com.dbms.csmq.view.backing.impact;

import com.dbms.csmq.CSMQBean;
import com.dbms.csmq.UserBean;
import com.dbms.csmq.view.backing.whod.WhodSourceTermSearchBean;
import com.dbms.csmq.view.backing.whod.WhodUtils;
import com.dbms.csmq.view.backing.whod.WhodWizardBean;
import com.dbms.csmq.view.backing.whod.WhodWizardSearchBean;
import com.dbms.csmq.view.hierarchy.GenericTreeNode;
import com.dbms.csmq.view.hierarchy.HierarchyAccessor;
import com.dbms.csmq.view.hierarchy.WhodNewPTListBean;
import com.dbms.csmq.view.impact.WhodFutureImpactHierarchyBean;
import com.dbms.csmq.view.impact.WhodMedDRAImpactHierarchyBean;
import com.dbms.util.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.RichPopup;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.data.RichTreeTable;
import oracle.adf.view.rich.component.rich.layout.RichToolbar;
import oracle.adf.view.rich.component.rich.output.RichImage;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.dnd.DnDAction;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.DropEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;
import oracle.jbo.uicli.binding.JUCtrlHierNodeBinding;

import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.model.RowKeySet;

public class WhodImpactAnalysisBean extends HierarchyAccessor{
    private String showImpact = CSMQBean.TRUE;
    private String mqType = "";//CSMQBean.NMQ;
    WhodFutureImpactHierarchyBean futureImpactHierarchyBean = (WhodFutureImpactHierarchyBean) AdfFacesContext.getCurrentInstance().getPageFlowScope().get("WhodFutureImpactHierarchyBean");
    CSMQBean cSMQBean = (CSMQBean)ADFContext.getCurrent().getApplicationScope().get("CSMQBean");
    UserBean userBean = (UserBean)ADFContext.getCurrent().getSessionScope().get("UserBean");
    WhodWizardSearchBean nMQWizardSearchBean = (WhodWizardSearchBean) AdfFacesContext.getCurrentInstance().getPageFlowScope().get("WhodWizardSearchBean");
    WhodWizardBean nMQWizardBean = WhodUtils.getWhodWizardBean();
    ImpactAnalysisUIBean impactAnalysisUIBean = null;
    
    private RichTreeTable preferedTermSourceTree;
    private RichTreeTable medDRATree;
    private RichTreeTable futureTree;
    private RichTreeTable hierarchySourceTree;
    
    
    
    private String paramReleaseGroup;
    private String parentSOCcontentID;
    private String currentTermName;
    private String activationGroups;
    private String activeDictionaryName;
    private String currentDictId;
    
    // FOR WORKFLOW
    private String currentState = CSMQBean.STATE_PROPOSED;
    private String currentReasonForApproval;
    
    private String currentPredictGroups;
    private String currentContentCode;
    
    int mode;
    
    private String paramFutureShowNonImpacted = CSMQBean.TRUE;
    private String paramFutureSort = "SCOPE";
    private String paramFuturePrimaryOnly = CSMQBean.FALSE;
    private String paramFutureScope = CSMQBean.FULL_NMQ_SMQ;

    private String paramMedDRAShowNonImpacted = CSMQBean.TRUE;
    private String paramMedDRASort = "SCOPE";
    private String paramMedDRAPrimaryOnly = CSMQBean.FALSE;
    private String paramMedDRAScope = CSMQBean.FULL_NMQ_SMQ;
    
    private String allGroups;
    private boolean futureShowImpacted;
    private boolean renderSave = false;
    private RichPopup.PopupHints hints = null;
    //private String socList = "";
    private Object socList;
    private Object newPTDisclosedKeys;

    public WhodImpactAnalysisBean() {
        
        super();
        System.out.println("START: ImpactAnalysisBean");
        activationGroups = applicationBean.getDefaultDraftReleaseGroup() + "," + applicationBean.getDefaultMedDRAReleaseGroup();
        //NMATDRAFT_AG,NMATMED_AG
        CSMQBean.logger.info(userBean.getCaller() + " @ImpactAnalysisBean");
        CSMQBean.logger.info(userBean.getCaller() + " New PT search groups: " + activationGroups);
        userBean.setCurrentMenuPath("Impact Assessment › MedDRA Version");
        userBean.setCurrentMenu("MEDDRA_IMPACT_ASSESSMENT");
        this.currentPredictGroups = cSMQBean.getDefaultMedDRAReleaseGroup();
        getDictionaryInfo();
        allGroups = this.defaultDraftGroupName + "," + this.defaultMedDRAGroupName + "," + this.defaultReleaseGroupName;
        nMQWizardBean.setPerformImpactPriorToExport(true);
        hints = new RichPopup.PopupHints();
        System.out.println("END: ImpactAnalysisBean");
        }


    private ImpactAnalysisUIBean getImpactAnalysisUIBean() {
        if (impactAnalysisUIBean == null) impactAnalysisUIBean = (ImpactAnalysisUIBean)ADFContext.getCurrent().getRequestScope().get("ImpactAnalysisUIBean");
        return impactAnalysisUIBean;
        }

    public void getDictionaryInfo() {
        System.out.println(">> Start getDictionaryInfo");
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("CurrentDictionaryVO1Iterator");
        ViewObject vo = dciterb.getViewObject();
        vo.setNamedWhereClauseParam("shortName", CSMQBean.defaultBaseDictionaryShortName);
        vo.executeQuery();
        Enumeration rows = dciterb.getRowSetIterator().enumerateRowsInRange();
        Row row = (Row)rows.nextElement();
        this.activeDictionaryName = Utils.getAsString(row, "Name");
        System.out.println(">> End getDictionaryInfo");
        }
    

    //    public void doSearch(ActionEvent actionEvent) {
    //        // THIS IS NOT USED ANY MORE
    //        impactAnalysisUIBean = (ImpactAnalysisUIBean)ADFContext.getCurrent().getRequestScope().get("ImpactAnalysisUIBean");
    //        impactAnalysisUIBean.getImpactSearchPopUp().show(hints);
    //
    //        String assessmentType = impactAnalysisUIBean.getCtrlImpact().getValue().toString();
    //        if (assessmentType.equals("INMQ")){
    //            this.showImpact = CSMQBean.TRUE;
    //            this.mqType = CSMQBean.NMQ;
    //            this.mode = CSMQBean.MODE_UPDATE_EXISTING;
    //            }
    //        else if (assessmentType.equals("NINMQ")){
    //            this.showImpact = CSMQBean.FALSE;
    //            this.mqType = CSMQBean.NMQ;
    //            this.mode = CSMQBean.MODE_UPDATE_EXISTING;
    //            }
    //        else if (assessmentType.equals("ISMQ")){
    //            this.showImpact = CSMQBean.TRUE;
    //            this.mqType = CSMQBean.SMQ;
    //            this.mode = CSMQBean.MODE_UPDATE_SMQ;
    //            }
    //        else if (assessmentType.equals("NISMQ")){
    //            this.showImpact = CSMQBean.FALSE;
    //            this.mqType = CSMQBean.SMQ;
    //            this.mode = CSMQBean.MODE_UPDATE_SMQ;
    //            }
    //
    //        CSMQBean.logger.info(userBean.getCaller() + " ** PERFORMING SEARCH **");
    //        CSMQBean.logger.info(userBean.getCaller() + " showImpact: " + this.showImpact);
    //        CSMQBean.logger.info(userBean.getCaller() + " mqType: " + this.mqType);
    //        CSMQBean.logger.info(userBean.getCaller() + " killSwitch: " + CSMQBean.KILL_SWITCH_OFF);
    //        CSMQBean.logger.info(userBean.getCaller() + " ***********************");
    //
    //        BindingContext bc = BindingContext.getCurrent();
    //        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
    //        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("ImpactSearchListVO1Iterator");
    //        ViewObject vo = dciterb.getViewObject();
    //
    //        vo.setNamedWhereClauseParam("showImpact", this.showImpact);
    //        vo.setNamedWhereClauseParam("mqType", this.mqType);
    //        vo.setNamedWhereClauseParam("killSwitch", CSMQBean.KILL_SWITCH_OFF);
    //
    //        vo.executeQuery();
    //
    //
    //
    //        impactAnalysisUIBean.getImpactSearchPopUp().show(hints);
    //
    //
    //
    //
    //        AdfFacesContext.getCurrentInstance().addPartialTarget(impactAnalysisUIBean.getCntrlImpactSearchResults());
    //        AdfFacesContext.getCurrentInstance().partialUpdateNotify(impactAnalysisUIBean.getCntrlImpactSearchResults());
    //        AdfFacesContext.getCurrentInstance().addPartialTarget(impactAnalysisUIBean.getCntrlTrain());
    //        AdfFacesContext.getCurrentInstance().partialUpdateNotify(impactAnalysisUIBean.getCntrlTrain());
    //        AdfFacesContext.getCurrentInstance().addPartialTarget(impactAnalysisUIBean.getPromotionToolBar());
    //        AdfFacesContext.getCurrentInstance().partialUpdateNotify(impactAnalysisUIBean.getPromotionToolBar());
    //        //clear the selected row
    //        RowKeySet rks = impactAnalysisUIBean.getCntrlImpactSearchResults().getSelectedRowKeys();
    //        rks.clear();
    //
    //
    //        }


    public String getShowImpact() {
        return showImpact;
    }

    public String getDefaultMedDRAGroupName() {
        return defaultMedDRAGroupName;
    }

    public String getMqType() {
        return mqType;
    }

    public void setShowImpact(String showImpact) {
        this.showImpact = showImpact;
    }

    public void setDefaultMedDRAGroupName(String groupName) {
        this.defaultMedDRAGroupName = groupName;
    }

    public void setMqType(String mQLevel) {
        this.mqType = mQLevel;
    }

    public void rowSelected(SelectionEvent selectionEvent) {
        CSMQBean.logger.info(userBean.getCaller() + " ***** ROW CHANGE ****");

        String source = ((RichTable)selectionEvent.getSource()).getId();
        String iterator = null;
        
        if (source.equalsIgnoreCase("TBL_MQ_N")) {
            iterator = "ImpactSearchListVO_MQ_N";
            this.showImpact = CSMQBean.FALSE;
            this.mqType = CSMQBean.SMQ;
            this.mode = CSMQBean.MODE_UPDATE_SMQ;
            }
        else if (source.equalsIgnoreCase("TBL_MQ_Y")) {
            iterator = "ImpactSearchListVO_MQ_Y";
            this.showImpact = CSMQBean.TRUE;
            this.mqType = CSMQBean.SMQ;
            this.mode = CSMQBean.MODE_UPDATE_SMQ;
            }
        else if (source.equalsIgnoreCase("TBL_NMQ_N")) {
            iterator = "ImpactSearchListVO_NMQ_N";
            this.showImpact = CSMQBean.FALSE;
            this.mqType = CSMQBean.NMQ;
            this.mode = CSMQBean.MODE_UPDATE_EXISTING;
            }
        else if (source.equalsIgnoreCase("TBL_NMQ_Y")) {
            iterator = "ImpactSearchListVO_NMQ_Y";
            this.showImpact = CSMQBean.TRUE;
            this.mqType = CSMQBean.NMQ;
            this.mode = CSMQBean.MODE_UPDATE_EXISTING;
        }



        resolveMethodExpression("#{bindings."+ iterator +".collectionModel.makeCurrent}", null, new Class[] { SelectionEvent.class }, new Object[] {selectionEvent});
        RichTable object = (RichTable)selectionEvent.getSource();
        Row row = null;
        for (Object facesRowKey : object.getSelectedRowKeys()) {
            object.setRowKey(facesRowKey);
            Object o = object.getRowData();
            JUCtrlHierNodeBinding rowData = (JUCtrlHierNodeBinding)o;
            row = rowData.getRow();
            }
        
        if (row == null) return;

        this.currentDictId = Utils.getAsString(row, "DictContentId");        
        this.currentContentCode = Utils.getAsString(row, "DictContentCode");  
        this.currentTermName = Utils.getAsString(row, "Term");
        this.activationGroups = CSMQBean.defaultMedDRAReleaseGroup;
        String smqNmq = Utils.getAsString(row, "NmqSmq");                        
        
        nMQWizardBean.setFooterInfo(this.currentTermName);
        
        String status = Utils.getAsString(row, "Status");
        
        CSMQBean.logger.info(userBean.getCaller() + " currentDictId: " + currentDictId);
            
        Hashtable result = WhodUtils.setDefaultIAState(this.currentDictId, smqNmq, userBean.getCurrentUser(), userBean.getCurrentUserRole());
        
        if (result != null) {
           this.setCurrentState((String)result.get("STATE"));   
            }
         
        // INF NOTES STUFF
        nMQWizardBean.setCurrentDictContentID(this.currentDictId);
        nMQWizardBean.setCurrentState(this.getCurrentState());
              
        nMQWizardSearchBean.setCurrentStatus(status);
        nMQWizardSearchBean.initForImpactAnalysis(currentContentCode, currentDictId, activationGroups);
        
        clearSearch ();
        
        
        getImpactAnalysisUIBean().getImpactSearchPopUp().cancel();
        
        if (medDRATree != null && medDRATree.getDisclosedRowKeys()!=null)
            medDRATree.getDisclosedRowKeys().clear();//to resolve NoRowAvailableException
                
        if (futureTree != null && futureTree.getDisclosedRowKeys()!=null)
            futureTree.getDisclosedRowKeys().clear();//to resolve NoRowAvailableException
               
        getImpactAnalysisUIBean().getCntrlExportButton().setDisabled(false);
        getImpactAnalysisUIBean().getCntrlExportDisplayedButtonLeft().setDisabled(false);
        getImpactAnalysisUIBean().getCntrlExportDisplayedButtonRight().setDisabled(false);
        getImpactAnalysisUIBean().getCntrlRefreshButton().setDisabled(false);
        getImpactAnalysisUIBean().getCntrlExportButton().setDisabled(false);
        
        AdfFacesContext.getCurrentInstance().addPartialTarget(getImpactAnalysisUIBean().getPromotionToolBar());
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(getImpactAnalysisUIBean().getPromotionToolBar());
        
        AdfFacesContext.getCurrentInstance().addPartialTarget(getImpactAnalysisUIBean().getCntrlImpactLeftToolbar());
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(getImpactAnalysisUIBean().getCntrlImpactLeftToolbar());
        
        AdfFacesContext.getCurrentInstance().addPartialTarget(getImpactAnalysisUIBean().getCntrlImpactRightToolbar());
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(getImpactAnalysisUIBean().getCntrlImpactRightToolbar());
        
        
        
        
        

        AdfFacesContext.getCurrentInstance().addPartialTarget(getImpactAnalysisUIBean().getCntrlTrain());
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(getImpactAnalysisUIBean().getCntrlTrain());
        AdfFacesContext.getCurrentInstance().addPartialTarget(getImpactAnalysisUIBean().getPromotionToolBar());
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(getImpactAnalysisUIBean().getPromotionToolBar());
        //clear the selected row
        //RowKeySet rks = impactAnalysisUIBean.getCntrlImpactSearchResults().getSelectedRowKeys();
        //rks.clear();

        
        refreshTrees(); 
        }
    
    
    private void clearSearch () {
        try {
            /*
            BindingContext bc = BindingContext.getCurrent();
            DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
            DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("ImpactSearchListVO1Iterator");
            ViewObject vo = dciterb.getViewObject();
            vo.setNamedWhereClauseParam("killSwitch", CSMQBean.KILL_SWITCH_ON);
            vo.executeQuery();
            impactAnalysisUIBean = (ImpactAnalysisUIBean)ADFContext.getCurrent().getRequestScope().get("ImpactAnalysisUIBean");
            AdfFacesContext.getCurrentInstance().addPartialTarget(impactAnalysisUIBean.getCntrlImpactSearchResults());
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(impactAnalysisUIBean.getCntrlImpactSearchResults());
            */
            //reset the filters
           
           
            if (getImpactAnalysisUIBean().getCntrlSearchResultsTBL_NMQ_Y() != null && getImpactAnalysisUIBean().getCntrlSearchResultsTBL_NMQ_Y().getDisclosedRowKeys()!=null )
                getImpactAnalysisUIBean().getCntrlSearchResultsTBL_NMQ_Y().getDisclosedRowKeys().clear();
            
            if (getImpactAnalysisUIBean().getCntrlSearchResultsTBL_MQ_Y() != null && getImpactAnalysisUIBean().getCntrlSearchResultsTBL_MQ_Y().getDisclosedRowKeys()!=null )
                getImpactAnalysisUIBean().getCntrlSearchResultsTBL_MQ_Y().getDisclosedRowKeys().clear();
            
            if (getImpactAnalysisUIBean().getCntrlSearchResultsTBL_NMQ_N() != null && getImpactAnalysisUIBean().getCntrlSearchResultsTBL_NMQ_N().getDisclosedRowKeys()!=null )
                getImpactAnalysisUIBean().getCntrlSearchResultsTBL_NMQ_N().getDisclosedRowKeys().clear();
            
            if (getImpactAnalysisUIBean().getCntrlSearchResultsTBL_MQ_N() != null && getImpactAnalysisUIBean().getCntrlSearchResultsTBL_MQ_N().getDisclosedRowKeys()!=null )
                getImpactAnalysisUIBean().getCntrlSearchResultsTBL_MQ_N().getDisclosedRowKeys().clear();
           
            getImpactAnalysisUIBean().getCntrlShowImpactedFilterExisting().setValue(false);
            getImpactAnalysisUIBean().getCntrlShowPrimaryFilterExisting().setValue(false);
            getImpactAnalysisUIBean().getCntrlShowSortFilterExisting().setValue("TERM");
            getImpactAnalysisUIBean().getCntrlShowImpactedFilterFuture().setValue(false);
            getImpactAnalysisUIBean().getCntrlShowPrimaryFilterFuture().setValue(false);
            getImpactAnalysisUIBean().getCntrlShowSortFilterFuture().setValue("TERM");
            
            getImpactAnalysisUIBean().getCntrlShowImpactedFilterExisting().resetValue();
            getImpactAnalysisUIBean().getCntrlShowPrimaryFilterExisting().resetValue();
            getImpactAnalysisUIBean().getCntrlShowSortFilterExisting().resetValue();
            getImpactAnalysisUIBean().getCntrlShowImpactedFilterFuture().resetValue();
            getImpactAnalysisUIBean().getCntrlShowPrimaryFilterFuture().resetValue();
            getImpactAnalysisUIBean().getCntrlShowSortFilterFuture().resetValue();
            
            this.paramFutureShowNonImpacted = CSMQBean.TRUE;
            this.paramFuturePrimaryOnly = CSMQBean.FALSE;
            this.paramMedDRAShowNonImpacted = CSMQBean.TRUE;
            this.paramMedDRAPrimaryOnly = CSMQBean.FALSE;
            this.paramFutureSort = "TERM";
            this.paramMedDRASort = "SCOPE";
            
            AdfFacesContext.getCurrentInstance().addPartialTarget (getImpactAnalysisUIBean().getCntrlFutureMenu());
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(getImpactAnalysisUIBean().getCntrlFutureMenu());
            
            AdfFacesContext.getCurrentInstance().addPartialTarget (getImpactAnalysisUIBean().getCntrlExistingMenu());
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(getImpactAnalysisUIBean().getCntrlExistingMenu());
            }
        catch (Exception e) {}
        }
    
    
    public void refreshTrees () {
        refreshFutureTree(null);
        refreshMedDRATree();
        }

    
    public void refreshFutureTree () {
        refreshFutureTree(null);
    }
    
    
    public void refreshMedDRATree () {
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding medDRAImpactVO1Iterator = (DCIteratorBinding)binding.get("MedDRAImpactVO1Iterator1");
        ViewObject medDRATreeVO = medDRAImpactVO1Iterator.getViewObject();
        
        CSMQBean.logger.info(userBean.getCaller() + " UPDATING: " +  allGroups); 
        medDRATreeVO.setNamedWhereClauseParam("activationGroup", allGroups);
        medDRATreeVO.setNamedWhereClauseParam("dictContentID", this.currentDictId);
        medDRATreeVO.setNamedWhereClauseParam("sortKey", this.paramMedDRASort);
        medDRATreeVO.setNamedWhereClauseParam("showNonImpacted", this.paramMedDRAShowNonImpacted);
        medDRATreeVO.setNamedWhereClauseParam("returnPrimLinkPath", this.paramMedDRAPrimaryOnly);
        medDRATreeVO.setNamedWhereClauseParam("scopeFilter", this.paramMedDRAScope);
            
        medDRATreeVO.setNamedWhereClauseParam("maxLevels", CSMQBean.getProperty("HIERARCHY_SUBSEQUENT_FETCH"));
        medDRATreeVO.setNamedWhereClauseParam("startLevel", 0);
        
        CSMQBean.logger.info(userBean.getCaller() + " Iterator: " + "MedDRAImpactVO1Iterator1");
        CSMQBean.logger.info(userBean.getCaller() + " activationGroup: " + allGroups);
        CSMQBean.logger.info(userBean.getCaller() + " dictContentID: " + this.currentDictId);
        CSMQBean.logger.info(userBean.getCaller() + " sortKey: " + this.paramMedDRASort);
        CSMQBean.logger.info(userBean.getCaller() + " showNonImpacted: " + this.paramMedDRAShowNonImpacted);
        CSMQBean.logger.info(userBean.getCaller() + " returnPrimLinkPath: " + this.paramMedDRAPrimaryOnly);
        System.out.println("scopeFilter: " + this.paramMedDRAScope);
        CSMQBean.logger.info(userBean.getCaller() + " maxLevels: " + CSMQBean.getProperty("HIERARCHY_SUBSEQUENT_FETCH"));
        CSMQBean.logger.info(userBean.getCaller() + " startLevel: " + 0);
        
        medDRATreeVO.executeQuery(); 
        WhodMedDRAImpactHierarchyBean medDRAImpactHierarchyBean = (WhodMedDRAImpactHierarchyBean) AdfFacesContext.getCurrentInstance().getPageFlowScope().get("WhodMedDRAImpactHierarchyBean");
        medDRAImpactHierarchyBean.init();
        AdfFacesContext.getCurrentInstance().addPartialTarget(medDRATree);
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(medDRATree);
        }
    
    
    public void refreshFutureTree (ActionEvent actionEvent) {
    
        
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding draftImpactVO1Iterator = (DCIteratorBinding)binding.get("DraftImpactVO1Iterator1");
        ViewObject draftTree = draftImpactVO1Iterator.getViewObject();
        //String bothGroups = this.defaultDraftGroupName + "," + this.defaultMedDRAGroupName;
        CSMQBean.logger.info(userBean.getCaller() + " UPDATING: " +  allGroups); 
        draftTree.setNamedWhereClauseParam("activationGroup", allGroups);
        draftTree.setNamedWhereClauseParam("dictContentID", this.currentDictId);
        
            
        draftTree.setNamedWhereClauseParam("activationGroup", allGroups);
        draftTree.setNamedWhereClauseParam("dictContentID", this.currentDictId);
        draftTree.setNamedWhereClauseParam("sortKey", this.paramFutureSort);
        draftTree.setNamedWhereClauseParam("showNonImpacted", this.paramFutureShowNonImpacted);
        draftTree.setNamedWhereClauseParam("returnPrimLinkPath", this.paramFuturePrimaryOnly);
        draftTree.setNamedWhereClauseParam("scopeFilter", this.paramFutureScope);
        draftTree.setNamedWhereClauseParam("maxLevels", CSMQBean.getProperty("HIERARCHY_SUBSEQUENT_FETCH"));
        draftTree.setNamedWhereClauseParam("startLevel", 0);
        
        CSMQBean.logger.info(userBean.getCaller() + " Iterator: " + "DraftImpactVO1Iterator1");
        CSMQBean.logger.info(userBean.getCaller() + " activationGroup: " + allGroups);
        CSMQBean.logger.info(userBean.getCaller() + " dictContentID: " + this.currentDictId);
        CSMQBean.logger.info(userBean.getCaller() + " sortKey: " + this.paramFutureSort);
        CSMQBean.logger.info(userBean.getCaller() + " showNonImpacted: " + this.paramFutureShowNonImpacted);
        CSMQBean.logger.info(userBean.getCaller() + " returnPrimLinkPath: " + this.paramFuturePrimaryOnly);
        System.out.println("scopeFilter: " + this.paramFutureScope);
        CSMQBean.logger.info(userBean.getCaller() + " maxLevels: " + CSMQBean.getProperty("HIERARCHY_SUBSEQUENT_FETCH"));
        CSMQBean.logger.info(userBean.getCaller() + " startLevel: " + 0);
        
        draftTree.executeQuery();   
        WhodFutureImpactHierarchyBean futureImpactHierarchyBean = (WhodFutureImpactHierarchyBean) AdfFacesContext.getCurrentInstance().getPageFlowScope().get("WhodFutureImpactHierarchyBean");
        boolean hasScope = false;
        if (nMQWizardBean.getCurrentScope() != null)
            hasScope = nMQWizardBean.getCurrentScope().equals(CSMQBean.HAS_SCOPE);
            
        CSMQBean.logger.info(userBean.getCaller() + " hasScope: " + hasScope);
        futureImpactHierarchyBean.init(hasScope);
        AdfFacesContext.getCurrentInstance().addPartialTarget(futureTree);
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(futureTree);
        }
    
    
    public void setCurrentDictId(String currentDictId) {
        this.currentDictId = currentDictId;
    }

    public String getCurrentDictId() {
        return currentDictId;
    }

    

    public void setParamReleaseGroup(String paramReleaseGroup) {
        this.paramReleaseGroup = paramReleaseGroup;
    }

    public String getParamReleaseGroup() {
        return paramReleaseGroup;
    }

    
    
    public DnDAction onTreeDrop(DropEvent dropEvent) {
        showStatus(CSMQBean.MQ_MODIFIED);
        RichTreeTable sourceTree;
        if (dropEvent.getDragClientId().toString().indexOf("ptList") > 0) 
            sourceTree = preferedTermSourceTree;
        else
            sourceTree = hierarchySourceTree;
    
        ///  THIS WILL PROBABLY NEED TO BE FIXED TO USE THE OTHER TREES - TES 1/26/2012
        return processDragAndDropEvent(dropEvent, sourceTree, futureTree, futureImpactHierarchyBean.getTreemodel(), Integer.parseInt(CSMQBean.FULL_NMQ_SMQ));
        }
    
    
    public void onTreeNodeDelete(ActionEvent actionEvent) { 
        showStatus(CSMQBean.MQ_MODIFIED);
        processTreeNodeDelete(futureTree, futureImpactHierarchyBean.getTreemodel());
        }

    
    public void deleteSelected(DialogEvent dialogEvent) {
        processDeleteSelected(dialogEvent, futureTree, futureImpactHierarchyBean.getTreemodel());
        }
    
    
    
    public void SOCChanged(ValueChangeEvent valueChangeEvent) {
        ArrayList list = new ArrayList(Arrays.asList(valueChangeEvent.getNewValue()));
        String retVal = "";//(String)list.get(0);;//
        
        for (Object obj : list) 
            retVal += obj.toString().trim();
        
        retVal = retVal.replace("[", "");
        retVal = retVal.replace("]", "");
        retVal = retVal.replace(',', '^');
        
        socList = retVal;
        }
    
    
    public void refreshNewPTList () {
        
        CSMQBean.logger.info(userBean.getCaller() + " CREATING NEW PT TREE");
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("NewPTsVO1Iterator");
        ViewObject newPTsVO1Iterator = dciterb.getViewObject();
        
        String groups = CSMQBean.defaultMEDSMQReleaseGroup + "^" + CSMQBean.defaultMedDRAReleaseGroup;
        
        newPTsVO1Iterator.setNamedWhereClauseParam("dictionaryName", CSMQBean.defaultBaseDictionaryShortName);
        newPTsVO1Iterator.setNamedWhereClauseParam("releaseGroups", groups);
        newPTsVO1Iterator.setNamedWhereClauseParam("contentIDs", getSOCList());      
        
        //DEFAULT_MEDSMQ_RELEASE_GROUP
        //DEFAULT_MEDDRA_RELEASE_GROUP
        
        CSMQBean.logger.info(userBean.getCaller() + " NewPTsVO1Iterator");
        CSMQBean.logger.info(userBean.getCaller() + " dictionaryName: " + CSMQBean.defaultBaseDictionaryShortName);
        CSMQBean.logger.info(userBean.getCaller() + " releaseGroups: " + groups);
        CSMQBean.logger.info(userBean.getCaller() + " contentIDs: " + socList);
        
        newPTsVO1Iterator.executeQuery();
        
        if ( preferedTermSourceTree.getDisclosedRowKeys() !=null)
            preferedTermSourceTree.getDisclosedRowKeys().clear();//to resolve NoRowAvailableException
        
        AdfFacesContext.getCurrentInstance().addPartialTarget(preferedTermSourceTree);
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(preferedTermSourceTree);
        
        WhodNewPTListBean newPTListBean = (WhodNewPTListBean) AdfFacesContext.getCurrentInstance().getPageFlowScope().get("WhodNewPTListBean");
        newPTListBean.init();

        CSMQBean.logger.info(userBean.getCaller() + " DONE CREATING NEW  PT TREE");
    }
    
    
    private String getSOCList () {
        String SOCListString = "";
        
        Object selProd = getImpactAnalysisUIBean().getCntlSOCList().getValue();
        if (selProd instanceof java.lang.String) {
            String temp = selProd.toString();
            temp.replace("[", "");
            temp.replace("]", "");
            SOCListString = temp;
            } 
        else {
            List selected = (List)getImpactAnalysisUIBean().getCntlSOCList().getValue();
            if (selected != null) {
                String temp = "";
                for (Object s : selected)
                    temp = temp + s + "^";

                temp.replace("[", "");
                temp.replace("]", "");

                if (temp != null & temp.length() > 0)
                    SOCListString= temp.substring(0, temp.length() - 1);
            }
        }
        return SOCListString;
    }
    
    private RichToolbar cntrlStatusBar;

    private RichImage iconMQChanged;
    private RichImage iconMQSaveError;
    private RichImage iconMQSaved;
    
    
    
    public void showStatus (int code) {
    
        this.iconMQChanged.setVisible(false);
        this.iconMQSaveError.setVisible(false);
        this.iconMQSaved.setVisible(false);
        
        switch (code) {
            case CSMQBean.MQ_SAVED:
                this.iconMQSaved.setVisible(true);
                break;
            case CSMQBean.MQ_SAVE_ERROR:
                this.iconMQSaveError.setVisible(true);
                break;
            case CSMQBean.MQ_MODIFIED:
                this.iconMQChanged.setVisible(true);
                break;
            }
        
        AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlStatusBar); 
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlStatusBar);
        }

    public void updateRelations(ActionEvent actionEvent) {
        
        int result = processUpdateRelations(futureTree, this.currentDictId);
        if (result == 0) showStatus(CSMQBean.MQ_SAVED);
        else showStatus(CSMQBean.MQ_SAVE_ERROR);
        refreshFutureTree(null);
        }

    

    public void setParentSOCcontentID(String parentSOCcontentID) {
        this.parentSOCcontentID = parentSOCcontentID;
    }

    public String getParentSOCcontentID() {
        this.parentSOCcontentID = getImpactAnalysisUIBean().getCntlSOCList().getValue().toString();
        return parentSOCcontentID;
    }

    public void setActivationGroups(String activationGroups) {
        this.activationGroups = activationGroups;
    }

    public String getActivationGroups() {
        return activationGroups;
    }
    


    public void clearMedDRATree () {
        RichTreeTable targetTree = medDRATree;
        // Clear keys
        if (targetTree != null && targetTree.getDisclosedRowKeys()!=null )
            targetTree.getDisclosedRowKeys().clear();//to resolve NoRowAvailableException
        
        // REQUERY 
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("MedDRAImpactVO1Iterator1");
        ViewObject vo = dciterb.getViewObject();

        vo.setNamedWhereClauseParam("dictContentID", 0);
        vo.executeQuery();
        
        AdfFacesContext.getCurrentInstance().addPartialTarget(targetTree); 
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(targetTree);
        }

    /*  public void showNewPTs(ActionEvent actionEvent) {
        UIComponent source = (UIComponent) actionEvent.getSource();
        refreshNewPTList ("ALL");
        RichPopup.PopupHints hints = new RichPopup.PopupHints();
        hints.add(RichPopup.PopupHints.HintTypes.HINT_ALIGN_ID, source)
              .add(RichPopup.PopupHints.HintTypes.HINT_LAUNCH_ID, source)
              .add(RichPopup.PopupHints.HintTypes.HINT_ALIGN, 
                   RichPopup.PopupHints.AlignTypes.ALIGN_BEFORE_START);
        getImpactAnalysisUIBean().getNewPreferedTermsPopup().show(hints);
    } */

    

    public void reviewed(DialogEvent dialogEvent) {        
        Hashtable result = WhodUtils.changeState(this.currentDictId, CSMQBean.IA_STATE_REVIEWED, userBean.getCurrentUser(), userBean.getUserRole(), null, null, cSMQBean.getDefaultDraftReleaseGroup());
        if (result != null) {
            this.currentState = (String)result.get("STATE");
            this.currentReasonForApproval = (String)result.get("REASON");         
            AdfFacesContext.getCurrentInstance().addPartialTarget(getImpactAnalysisUIBean().getPromotionToolBar());
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(getImpactAnalysisUIBean().getPromotionToolBar());
            }
        }

    public void approve(DialogEvent dialogEvent) {        
        Hashtable result = WhodUtils.changeState(this.currentDictId, CSMQBean.IA_STATE_APPROVED, userBean.getCurrentUser(), userBean.getUserRole(), null, null, cSMQBean.getDefaultDraftReleaseGroup());
        if (result != null) {
            this.currentState = (String)result.get("STATE");
            this.currentReasonForApproval = (String)result.get("REASON");         
            AdfFacesContext.getCurrentInstance().addPartialTarget(getImpactAnalysisUIBean().getPromotionToolBar());
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(getImpactAnalysisUIBean().getPromotionToolBar());
            }
        }

    public void delete(DialogEvent dialogEvent) {
        if (WhodUtils.delete(this.currentDictId, CSMQBean.defaultDraftReleaseGroup)) {
            AdfFacesContext.getCurrentInstance().addPartialTarget(getImpactAnalysisUIBean().getPromotionToolBar());
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(getImpactAnalysisUIBean().getPromotionToolBar());
            }
        }

    public void demoteToPendingIA (DialogEvent dialogEvent)  {
        Hashtable result = WhodUtils.changeState(this.currentDictId, CSMQBean.STATE_PENDING_IMPACT_ASSESSMENT, userBean.getCurrentUser(), userBean.getUserRole(), null, null, cSMQBean.getDefaultDraftReleaseGroup());
        if (result != null) {
            this.currentState = (String)result.get("STATE");
            this.currentReasonForApproval = (String)result.get("REASON");
            AdfFacesContext.getCurrentInstance().addPartialTarget(getImpactAnalysisUIBean().getPromotionToolBar());
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(getImpactAnalysisUIBean().getPromotionToolBar());
            }
        }
    
    
    public void setCurrentState(String currentState) {
        this.currentState = currentState;
    }

    public String getCurrentState() {
        return currentState;
    }

    public void existingPrimLinkFlagChanged(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent.getNewValue().toString().equals(valueChangeEvent.getOldValue())) return; // it didn't change
        refreshMedDRATree();
    }

    public void futurePrimLinkFlagChanged(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent.getNewValue().toString().equals(valueChangeEvent.getOldValue())) return; // it didn't change
        refreshFutureTree(null);
    }

    public void setParamFutureShowNonImpacted(String paramFutureImpactedOnly) {
        this.paramFutureShowNonImpacted = paramFutureImpactedOnly;
    }

    public String getParamFutureShowNonImpacted() {
        return paramFutureShowNonImpacted;
    }

    public void setParamFutureSort(String paramFutureSort) {
        this.paramFutureSort = paramFutureSort;
    }

    public String getParamFutureSort() {
        return paramFutureSort;
    }

    public void futureParamsChanged(ValueChangeEvent valueChangeEvent) {
        String source = valueChangeEvent.getComponent().getId();
        
        if (source.equals("paramFutureSIO")) {
            paramFutureShowNonImpacted = (Boolean)valueChangeEvent.getNewValue() ? CSMQBean.FALSE : CSMQBean.TRUE;
            }
        if (source.equals("paramFutureSPO")) {
            paramFuturePrimaryOnly = (Boolean)valueChangeEvent.getNewValue() ? CSMQBean.TRUE : CSMQBean.FALSE;
            }
        if (source.equals("paramFutureSort")) {
            paramFutureSort = ((String)valueChangeEvent.getNewValue());
            }
        if (source.equals("paramFutureScope")) {
            paramFutureScope = ((String)valueChangeEvent.getNewValue());
            }
    
        //refreshFutureTree(null); 
        }


    public void existingParamsChanged(ValueChangeEvent valueChangeEvent) {
        String source = valueChangeEvent.getComponent().getId();
        
        if (source.equals("paramMedDRASIO")) {
            paramMedDRAShowNonImpacted = (Boolean)valueChangeEvent.getNewValue() ? CSMQBean.FALSE : CSMQBean.TRUE;
            }
        if (source.equals("paramMedDRASPO")) {
            paramMedDRAPrimaryOnly = (Boolean)valueChangeEvent.getNewValue() ? CSMQBean.TRUE : CSMQBean.FALSE;
            }
        if (source.equals("paramMedDRASort")) {
            paramMedDRASort = ((String)valueChangeEvent.getNewValue());
            }
        if (source.equals("paramMedDRAScope")) {
            paramMedDRAScope = ((String)valueChangeEvent.getNewValue());
            }
    
        //refreshMedDRATree(); 
        }
    

    public void setParamFutureScope(String paramFutureScope) {
        this.paramFutureScope = paramFutureScope;
    }

    public String getParamFutureScope() {
        return paramFutureScope;
    }

    public void setParamFuturePrimaryOnly(String paramFuturePrimaryOnly) {
        this.paramFuturePrimaryOnly = paramFuturePrimaryOnly;
    }

    public String getParamFuturePrimaryOnly() {
        return paramFuturePrimaryOnly;
    }

    public void setParamMedDRAShowNonImpacted(String paramMedDRAImpactedOnly) {
        this.paramMedDRAShowNonImpacted = paramMedDRAImpactedOnly;
    }

    public String getParamMedDRAShowNonImpacted() {
        return paramMedDRAShowNonImpacted;
    }

    public void setParamMedDRASort(String paramMedDRASort) {
        this.paramMedDRASort = paramMedDRASort;
    }

    public String getParamMedDRASort() {
        return paramMedDRASort;
    }

    public void setParamMedDRAPrimaryOnly(String paramMedDRAPrimaryOnly) {
        this.paramMedDRAPrimaryOnly = paramMedDRAPrimaryOnly;
    }

    public String getParamMedDRAPrimaryOnly() {
        return paramMedDRAPrimaryOnly;
    }

    public void setParamMedDRAScope(String paramMedDRAScope) {
        this.paramMedDRAScope = paramMedDRAScope;
    }

    public String getParamMedDRAScope() {
        return paramMedDRAScope;
    }

    public void setCurrentTermName(String currentTerm) {
        this.currentTermName = currentTerm;
    }

    public String getCurrentTermName() {
        return currentTermName;
    }

    public void setAllGroups(String allGroups) {
        this.allGroups = allGroups;
    }

    public String getAllGroups() {
        return allGroups;
    }

    public void hierarchyPopUpFetch(PopupFetchEvent popupFetchEvent) {
        WhodSourceTermSearchBean nMQSourceTermSearchBean = (WhodSourceTermSearchBean)AdfFacesContext.getCurrentInstance().getPageFlowScope().get("WhodSourceTermSearchBean");
        //nMQSourceTermSearchBean.refreshLevelList(applicationBean.getDefaultFilterDictionaryShortName());
       if (paramFutureShowNonImpacted.equals(CSMQBean.FALSE)) {
            paramFutureShowNonImpacted = CSMQBean.TRUE;
            futureShowImpacted = false;
            refreshFutureTree(null);     
            AdfFacesContext.getCurrentInstance().addPartialTarget(getImpactAnalysisUIBean().getCntrlFutureMenu());
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(getImpactAnalysisUIBean().getCntrlFutureMenu());
            }
    }

    public void newPTPopUpFetch(PopupFetchEvent popupFetchEvent) {
       //here
       
       if (paramFutureShowNonImpacted.equals(CSMQBean.FALSE)) {
            paramFutureShowNonImpacted = CSMQBean.TRUE;
            futureShowImpacted = false;
            refreshFutureTree(null);
            AdfFacesContext.getCurrentInstance().addPartialTarget(getImpactAnalysisUIBean().getCntrlFutureMenu());
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(getImpactAnalysisUIBean().getCntrlFutureMenu());
            }
    }

    public void setFutureShowImpacted(boolean futureShowImpacted) {
        this.futureShowImpacted = futureShowImpacted;
    }

    public boolean getFutureShowImpacted() {
        return futureShowImpacted;
    }

    public void setRenderSave(boolean renderSave) {
        this.renderSave = renderSave;
    }

    public boolean isRenderSave() {
        this.renderSave = false;
        
        //if (this.mqType == CSMQBean.NMQ && (
        
        
        if (this.currentState != null && (this.currentState.equals(CSMQBean.STATE_PENDING_IMPACT_ASSESSMENT) ||
            this.currentState.equals(CSMQBean.IA_STATE_APPROVED) ||
            this.currentState.equals(CSMQBean.IA_STATE_REVIEWED)
            )) this.renderSave = true;
        return this.renderSave;
    }

    public void nodeChanged(ValueChangeEvent valueChangeEvent) {
    
        
        /* soc3 = scope
         * soc2 = category
         * ot6 = weight
         */
        
        Object o = valueChangeEvent;
        RowKeySet rks = futureTree.getSelectedRowKeys();
        Iterator rksIterator = rks.iterator();
        List key = (List)rksIterator.next();
        
        int rowKey = Integer.parseInt(key.get(1).toString());
        GenericTreeNode node = (GenericTreeNode)futureTree.getRowData(rowKey);
        
        // TEST TO SEE IF IT'S ALREADY BEEN EDITED
        GenericTreeNode oldNode = updates.get(node.getPrikey());
        if (oldNode != null) node = oldNode;
        
        // UPDATE THE NODE (EITHER THE OLD OR THE NEW) WITH THE CHANGED VALUES
        if (valueChangeEvent.getSource().toString().indexOf("soc3") > -1)
            node.setFormattedScope(valueChangeEvent.getNewValue().toString());
        
        if (valueChangeEvent.getSource().toString().indexOf("soc2") > -1)
            node.setTermCategory(valueChangeEvent.getNewValue().toString());
        
        if (valueChangeEvent.getSource().toString().indexOf("ot6") > -1)
            node.setTermWeight(valueChangeEvent.getNewValue().toString());
        
        if (oldNode == null)  // ADD IT SINCE IT'S NOT THERE ALREADY
            updates.put(node.getPrikey(), node);
    }

    public void refreshMedDRATree(ActionEvent actionEvent) {
        refreshMedDRATree();
    }
    
    public String doSearch() {
        // Add event code here...
        return null;
    }

    public void setCntrlStatusBar(RichToolbar cntrlStatusBar) {
        this.cntrlStatusBar = cntrlStatusBar;
    }

    public RichToolbar getCntrlStatusBar() {
        return cntrlStatusBar;
    }

    public void setIconMQChanged(RichImage iconMQChanged) {
        this.iconMQChanged = iconMQChanged;
    }

    public RichImage getIconMQChanged() {
        return iconMQChanged;
    }

    public void setIconMQSaveError(RichImage iconMQSaveError) {
        this.iconMQSaveError = iconMQSaveError;
    }

    public RichImage getIconMQSaveError() {
        return iconMQSaveError;
    }
    
    public void setIconMQSaved(RichImage iconMQSaved) {
        this.iconMQSaved = iconMQSaved;
    }

    public RichImage getIconMQSaved() {
        return iconMQSaved;
    }

    public void updateImpactSearchResults(ActionEvent actionEvent) {
        //this.doSearch(null);
    }

    public void impactSearchResultsPopUp(PopupFetchEvent popupFetchEvent) {
        clearSearch();
    }

    public void refreshNewPTList(ActionEvent actionEvent) {
        refreshNewPTList ();
    }

    public void setSocList(Object socList) {
        this.socList = socList;
    }

    public Object getSocList() {
        return socList;
    }

    public void setNewPTDisclosedKeys(Object newPTDisclosedKeys) {
        this.newPTDisclosedKeys = newPTDisclosedKeys;
    }

    public Object getNewPTDisclosedKeys() {
        return newPTDisclosedKeys;
    }
    
    public void setPreferedTermSourceTree(RichTreeTable sourceTree) {
        this.preferedTermSourceTree = sourceTree;
    }

    public RichTreeTable getPreferedTermSourceTree() {
        return preferedTermSourceTree;
    }
    
    

    public void setHierarchySourceTree(RichTreeTable hierarchySourceTree) {
        this.hierarchySourceTree = hierarchySourceTree;
    }

    public RichTreeTable getHierarchySourceTree() {
        return hierarchySourceTree;
    }
    public void setMedDRATree(RichTreeTable medDRATree) {
        this.medDRATree = medDRATree;
    }

    public RichTreeTable getMedDRATree() {
        return medDRATree;
    }

    public void setFutureTree(RichTreeTable futureTree) {
        this.futureTree = futureTree;
    }

    public RichTreeTable getFutureTree() {
        return futureTree;
    }
    
    
    
}
