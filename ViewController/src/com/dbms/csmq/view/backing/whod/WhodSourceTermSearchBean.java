package com.dbms.csmq.view.backing.whod;


import com.dbms.csmq.CSMQBean;
import com.dbms.csmq.view.backing.impact.ImpactAnalysisBean;
import com.dbms.csmq.view.backing.impact.ImpactSearchBean;
import com.dbms.csmq.view.hierarchy.GenericTreeNode;
import com.dbms.csmq.view.hierarchy.HierarchyAccessor;
import com.dbms.csmq.view.hierarchy.WhodHierarchySearchResultsBean;
import com.dbms.csmq.view.hierarchy.WhodTermHierarchyBean;
import com.dbms.csmq.view.hierarchy.WhodTermHierarchySourceBean;
import com.dbms.util.Utils;
import com.dbms.util.dml.DMLUtils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;
import oracle.adf.view.rich.component.rich.data.RichTable;
import oracle.adf.view.rich.component.rich.data.RichTreeTable;
import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;
import oracle.adf.view.rich.context.AdfFacesContext;
import oracle.adf.view.rich.dnd.DnDAction;
import oracle.adf.view.rich.event.DialogEvent;
import oracle.adf.view.rich.event.DropEvent;
import oracle.adf.view.rich.event.PopupCanceledEvent;
import oracle.adf.view.rich.event.PopupFetchEvent;

import oracle.jbo.Key;
import oracle.jbo.Row;
import oracle.jbo.RowSetIterator;
import oracle.jbo.ViewObject;
import oracle.jbo.server.DBTransaction;

import org.apache.myfaces.trinidad.event.SelectionEvent;
import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetTreeImpl;
import org.apache.myfaces.trinidad.model.TreeModel;


public class WhodSourceTermSearchBean extends HierarchyAccessor {

    private String paramDictionaryType = "NULL";
    private String paramDictionary = "NULL";
    private String paramLevel = "NULL";
    private String paramReleaseGroup = "NULL";
    private String paramTerm = "NULL";
    private String paramScope = "-1";
    private String paramSort = "TERM";
    private String paramPrimLinkFlag = CSMQBean.TRUE;
    private String paramNarrowScopeOnly = CSMQBean.FALSE;

    private boolean historicSearch = false;
    private boolean addRelationsSearch = false;
    private boolean impactSearch = false;
    private boolean multiSearch = false;

    boolean showBaseLevelSelItems = false; // this is the default
    boolean showFilterSelItems = true;

    private String currentDictId;
    private GenericTreeNode currentRowData;


    private WhodWizardBean nMQWizardBean;
    WhodTermHierarchySourceBean termHierarchySourceBean =
        (WhodTermHierarchySourceBean)AdfFacesContext.getCurrentInstance().getPageFlowScope().get("WhodTermHierarchySourceBean");
    WhodTermHierarchyBean termHierarchyBean =
        (WhodTermHierarchyBean)AdfFacesContext.getCurrentInstance().getPageFlowScope().get("WhodTermHierarchyBean");
    WhodSourceTermSearchUIBean nMQSourceTermSearchUIBean = null;
    private boolean hasScope = false;
    private String paramExtension;
    private RichSelectOneChoice controlDictionaryType;
    private RichSelectOneChoice controlLevel;

    private RichTreeTable multiHierarchySourceTree;
    private RichTreeTable controlMultiResultsTable;

    public void doSearch(ActionEvent actionEvent) {
        paramScope = "-1";
        CSMQBean.logger.info(userBean.getCaller() + " *** PRFORMING SEARCH ***");
        CSMQBean.logger.info(userBean.getCaller() + " Iterator: WhodHierarchySourceTermSearchVO1Iterator");
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("WhodHierarchySourceTermSearchVO1Iterator");
        ViewObject vo = dciterb.getViewObject();
        String paramTermVal = getParamTerm();
        if (null != paramTermVal && !paramTermVal.isEmpty()) {
            paramTermVal = paramTermVal.replace("'", "\''");
        }
        vo.setNamedWhereClauseParam("startDate", "");
        vo.setNamedWhereClauseParam("endDate", "");
        vo.setNamedWhereClauseParam("term", paramTermVal);
        vo.setNamedWhereClauseParam("activityStatus", "ALL");
        vo.setNamedWhereClauseParam("dictShortName", getParamDictionary());
        vo.setNamedWhereClauseParam("releaseStatus", CSMQBean.BOTH_ACTIVITY_STATUSES);
        vo.setNamedWhereClauseParam("activationGroup", CSMQBean.WILDCARD);
        vo.setNamedWhereClauseParam("MQGroup", CSMQBean.WILDCARD);
        vo.setNamedWhereClauseParam("product", CSMQBean.WILDCARD);
        vo.setNamedWhereClauseParam("MQCode", CSMQBean.WILDCARD);
        vo.setNamedWhereClauseParam("MQCriticalEvent", CSMQBean.WILDCARD);
        vo.setNamedWhereClauseParam("uniqueIDsOnly", CSMQBean.TRUE);
        vo.setNamedWhereClauseParam("filterForUser", CSMQBean.FALSE);
        vo.setNamedWhereClauseParam("currentUser", nMQWizardBean.getCurrentUser());
        vo.setNamedWhereClauseParam("killSwitch", CSMQBean.KILL_SWITCH_OFF);
        vo.setNamedWhereClauseParam("showNarrowScpOnly", getParamNarrowScopeOnly());
        vo.setNamedWhereClauseParam("MQScope", CSMQBean.WILDCARD);
        vo.setNamedWhereClauseParam("pState", CSMQBean.WILDCARD);
        vo.setNamedWhereClauseParam("pUserRole", "MQM");
        vo.setNamedWhereClauseParam("levelName", getParamLevel());
        CSMQBean.logger.info(userBean.getCaller() + " startDate: " + "");
        CSMQBean.logger.info(userBean.getCaller() + " endDate: " + "");
        CSMQBean.logger.info(userBean.getCaller() + " term: " + paramTermVal);
        CSMQBean.logger.info(userBean.getCaller() + " activityStatus: " + "ALL");
        CSMQBean.logger.info(userBean.getCaller() + " dictShortName: " + getParamDictionary());
        CSMQBean.logger.info(userBean.getCaller() + " releaseStatus: " + CSMQBean.BOTH_ACTIVITY_STATUSES);
        CSMQBean.logger.info(userBean.getCaller() + " activationGroup: " + CSMQBean.WILDCARD);
        CSMQBean.logger.info(userBean.getCaller() + " MQGroup: " + CSMQBean.WILDCARD);
        CSMQBean.logger.info(userBean.getCaller() + " product: " + CSMQBean.WILDCARD);
        CSMQBean.logger.info(userBean.getCaller() + " MQCode: " + CSMQBean.WILDCARD);
        CSMQBean.logger.info(userBean.getCaller() + " MQCriticalEvent: " + "%");
        CSMQBean.logger.info(userBean.getCaller() + " uniqueIDsOnly: " + CSMQBean.TRUE);
        CSMQBean.logger.info(userBean.getCaller() + " filterForUser: " + CSMQBean.FALSE);
        CSMQBean.logger.info(userBean.getCaller() + " currentUser: " + nMQWizardBean.getCurrentUser());
        CSMQBean.logger.info(userBean.getCaller() + " killSwitch: " + CSMQBean.KILL_SWITCH_OFF);
        CSMQBean.logger.info(userBean.getCaller() + " showNarrowScpOnly: " + getParamNarrowScopeOnly());
        CSMQBean.logger.info(userBean.getCaller() + " MQScope: " + CSMQBean.WILDCARD);
        CSMQBean.logger.info(userBean.getCaller() + " pState: " + CSMQBean.WILDCARD);
        CSMQBean.logger.info(userBean.getCaller() + " pUserRole: " + "MQM");
        CSMQBean.logger.info(userBean.getCaller() + " levelName: " + getParamLevel());
        vo.executeQuery();
        nMQSourceTermSearchUIBean =
                (WhodSourceTermSearchUIBean)ADFContext.getCurrent().getRequestScope().get("WhodSourceTermSearchUIBean");
        AdfFacesContext.getCurrentInstance().addPartialTarget(nMQSourceTermSearchUIBean.getControlResultsTable());
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(nMQSourceTermSearchUIBean.getControlResultsTable());
    }


    public void doMultiSearch(ActionEvent actionEvent) {
        paramScope = "-1";
        CSMQBean.logger.info(userBean.getCaller() + " *** PRFORMING SEARCH ***");
        CSMQBean.logger.info(userBean.getCaller() + " Iterator: WhodHierarchySourceTermSearchVO1Iterator");
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("WhodHierarchySourceTermSearchVO1Iterator");
        ViewObject vo = dciterb.getViewObject();
        vo.setNamedWhereClauseParam("dictionaryName", getParamDictionaryType());
        String paramTermVal = getParamTerm();
        if (null != paramTermVal && !paramTermVal.isEmpty()) {
            paramTermVal = paramTermVal.replace("'", "\''");
        }
        vo.setNamedWhereClauseParam("term", paramTermVal);
        vo.setNamedWhereClauseParam("levelName", getParamLevel());
        CSMQBean.logger.info(userBean.getCaller() + " term: " + paramTermVal);
        CSMQBean.logger.info(userBean.getCaller() + " dictShortName: " + getParamDictionary());
        CSMQBean.logger.info(userBean.getCaller() + " levelName: " + getParamLevel());
        vo.executeQuery();
        WhodHierarchySearchResultsBean hierarchySearchResultsBean =
            (WhodHierarchySearchResultsBean)ADFContext.getCurrent().getPageFlowScope().get("WhodHierarchySearchResultsBean");
        hierarchySearchResultsBean.init();
        AdfFacesContext.getCurrentInstance().addPartialTarget(controlMultiResultsTable);
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(controlMultiResultsTable);
    }


    public WhodSourceTermSearchBean() {
        nMQWizardBean = (WhodWizardBean)AdfFacesContext.getCurrentInstance().getPageFlowScope().get("WhodWizardBean");
    }


    // PARAMS

    public String getParamDictionaryType() {
        /*  try {
            nMQSourceTermSearchUIBean = (NMQSourceTermSearchUIBean)ADFContext.getCurrent().getRequestScope().get("NMQSourceTermSearchUIBean");
            this.paramDictionaryType = nMQSourceTermSearchUIBean.getControlDictionaryType().getValue().toString();
            }
        catch (Exception e) {} */
        this.paramDictionaryType = getControlDictionaryType().getValue().toString();
        return paramDictionaryType;
    }

    public String getParamDictionary() {
        try {
            if (getParamDictionaryType().equals("BASE"))
                this.paramDictionary = nMQWizardBean.getCurrentBaseDictionaryShortName();
            else
                this.paramDictionary = nMQWizardBean.getCurrentFilterDictionaryShortName();
        } catch (Exception e) {
        }
        return paramDictionary;
    }

    public String getParamLevel() {
        try {
            nMQSourceTermSearchUIBean =
                    (WhodSourceTermSearchUIBean)ADFContext.getCurrent().getRequestScope().get("WhodSourceTermSearchUIBean");
            this.paramLevel = getControlLevel().getValue().toString();
        } catch (Exception e) {
        }
        return paramLevel;
    }

    public String getParamReleaseGroup() {
        try {
            nMQSourceTermSearchUIBean =
                    (WhodSourceTermSearchUIBean)ADFContext.getCurrent().getRequestScope().get("WhodSourceTermSearchUIBean");
            if (nMQSourceTermSearchUIBean.getControlReleaseGroup() != null) {
                this.paramReleaseGroup = nMQSourceTermSearchUIBean.getControlReleaseGroup().getValue().toString();
            } else { // this is an overload to use the bean from the impact screen
                CSMQBean applicationBean = (CSMQBean)ADFContext.getCurrent().getApplicationScope().get("CSMQBean");
                this.paramReleaseGroup = applicationBean.getDefaultDraftReleaseGroup();
            }

        } catch (Exception e) {
        }
        return paramReleaseGroup;
    }

    public String getParamTerm() {
        paramTerm = CSMQBean.WILDCARD;
        nMQSourceTermSearchUIBean =
                (WhodSourceTermSearchUIBean)ADFContext.getCurrent().getRequestScope().get("WhodSourceTermSearchUIBean");
        if (nMQSourceTermSearchUIBean != null && nMQSourceTermSearchUIBean.getControlTerm() != null &&
            nMQSourceTermSearchUIBean.getControlTerm().getValue() != null &&
            !nMQSourceTermSearchUIBean.getControlTerm().getValue().toString().equalsIgnoreCase("null") &&
            nMQSourceTermSearchUIBean.getControlTerm().getValue().toString().length() > 0)
            paramTerm = nMQSourceTermSearchUIBean.getControlTerm().getValue().toString();
        return paramTerm;
    }


    public void rowChanged(SelectionEvent selectionEvent) {

        // SEE WHICH OF THE 3 SOURCES IT IS
        if (selectionEvent.getSource().toString().indexOf("hResults") > 0)
            this.historicSearch = true;
        else if (selectionEvent.getSource().toString().indexOf("rResults") > 0)
            this.addRelationsSearch = true;
        else if (selectionEvent.getSource().toString().indexOf("t5") > 0)
            this.multiSearch = true;
        else
            this.impactSearch = true;

        CSMQBean.logger.info(userBean.getCaller() + " ***** ROW CHANGE ****");

        RichTable sourceTable = (RichTable)selectionEvent.getSource();
        RowKeySet selectedEmps = sourceTable.getSelectedRowKeys();
        Iterator selectedEmpIter = selectedEmps.iterator();
        DCBindingContainer bindings = (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
        DCIteratorBinding empIter = bindings.findIteratorBinding("HierarchySourceTermSearchVO1Iterator");
        RowSetIterator empRSIter = empIter.getRowSetIterator();

        while (selectedEmpIter.hasNext()) {
            Key key = (Key)((List)selectedEmpIter.next()).get(0);
            Row currentRow = empRSIter.getRow(key); // current row
            System.out.println(currentRow.getAttribute("ContentId"));

            this.currentDictId = Utils.getAsString(currentRow, "ContentId");
            this.hasScope = Utils.getAsBoolean(currentRow, "Mqscp");

            CSMQBean.logger.info(userBean.getCaller() + " paramReleaseGroup: " + paramReleaseGroup);
            CSMQBean.logger.info(userBean.getCaller() + " currentDictId: " + currentDictId);
        }

        sourceTable.getSelectedRowKeys().clear(); // clear the selection - if there is only one, then the user can't select it & fire the event
        //if (!impactSearch) { //close it if its not an impact search - this MAY need to be changed for the other 2 types
        //sourceTable.getSelectedRowKeys().clear();  // clear the selection - if there is only one, then the user can't select it & fire the event
        //     if (nMQSourceTermSearchUIBean != null && nMQSourceTermSearchUIBean.getSearchPopUp() != null)
        //        nMQSourceTermSearchUIBean.getSearchPopUp().cancel();
        //      }

        refreshTree();

    }

    public void multiSelectRowChanged(SelectionEvent selectionEvent) {

        //RichTreeTable sourceTable = (RichTreeTable)selectionEvent.getSource();
        RichTreeTable sourceTable = controlMultiResultsTable;
        //clearKeys (targetTree);

        GenericTreeNode newRootNode = null;

        //RichTreeTable tree = targetTree;
        RowKeySet droppedValue = sourceTable.getSelectedRowKeys();

        Object[] keys = droppedValue.toArray();

        RowKeySet selectedRowKeys = sourceTable.getSelectedRowKeys();

        //Store original rowKey
        Object oldRowKey = sourceTable.getRowKey();
        try {

            if (selectedRowKeys != null) {
                Iterator iter = selectedRowKeys.iterator();
                if (iter != null && iter.hasNext()) {
                    Object rowKey = iter.next();
                    sourceTable.setRowKey(rowKey); //stamp row
                    GenericTreeNode rowData = (GenericTreeNode)sourceTable.getRowData();

                    this.currentDictId = rowData.getDictContentId();
                    this.hasScope = rowData.isHasScope();
                    this.currentRowData = rowData;

                    System.out.println(rowData);
                }
            }

        } finally {

            //Restore the original rowKey
            sourceTable.setRowKey(oldRowKey);

        }


        this.multiSearch = true;


        CSMQBean.logger.info(userBean.getCaller() + " ***** ROW CHANGE ****");

        //RichTreeTable sourceTable = (RichTreeTable)selectionEvent.getSource();
        //RowKeySet selectedEmps = sourceTable.getSelectedRowKeys();
        //Iterator selectedEmpIter = selectedEmps.iterator();
        //DCBindingContainer bindings = (DCBindingContainer)BindingContext.getCurrent().getCurrentBindingsEntry();
        //DCIteratorBinding empIter = bindings.findIteratorBinding("HierarchySourceTermSearchVO1Iterator");
        //RowSetIterator empRSIter = empIter.getRowSetIterator();

        //while (selectedEmpIter.hasNext()) {
        //Key key = (Key)((List)selectedEmpIter.next()).get(0);
        //Row currentRow = empRSIter.getRow(key); // current row
        // System.out.println(currentRow.getAttribute("ContentId"));

        //GenericTreeNode newRootNode = null;


        //RowKeySet rks = sourceTable.getSelectedRowKeys();
        //Iterator rksIterator = rks.iterator();
        //List firstSet = (List)rks.iterator().next();


        //}

        CSMQBean.logger.info(userBean.getCaller() + " paramReleaseGroup: " + paramReleaseGroup);
        CSMQBean.logger.info(userBean.getCaller() + " currentDictId: " + currentDictId);


        //sourceTable.getSelectedRowKeys().clear();  // clear the selection - if there is only one, then the user can't select it & fire the event
        //if (!impactSearch) { //close it if its not an impact search - this MAY need to be changed for the other 2 types
        //sourceTable.getSelectedRowKeys().clear();  // clear the selection - if there is only one, then the user can't select it & fire the event
        //     if (nMQSourceTermSearchUIBean != null && nMQSourceTermSearchUIBean.getSearchPopUp() != null)
        //        nMQSourceTermSearchUIBean.getSearchPopUp().cancel();
        //      }

        refreshTree();

    }


    public void refreshTree() {
        CSMQBean.logger.info(userBean.getCaller() + " *** CREATING SOURCE TREE ***");
        CSMQBean.logger.info(userBean.getCaller() + " Iterator: SourceTreeVO1Iterator");
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("WhodHierarchySourceRelationSearchVO1Iterator");
        ViewObject sourceTreeVO = dciterb.getViewObject();
        sourceTreeVO.setNamedWhereClauseParam("dictContentID", this.currentDictId);
        CSMQBean.logger.info(userBean.getCaller() + " dictContentID:" + this.currentDictId);
        sourceTreeVO.executeQuery();
        termHierarchySourceBean.init(currentRowData);

        if (impactSearch) {
            ImpactAnalysisBean impactAnalysisBean =
                (ImpactAnalysisBean)ADFContext.getCurrent().getPageFlowScope().get("ImpactAnalysisBean");
            AdfFacesContext.getCurrentInstance().addPartialTarget(impactAnalysisBean.getHierarchySourceTree());
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(impactAnalysisBean.getHierarchySourceTree());
        } else if (historicSearch) {
            ImpactSearchBean impactSearchBean =
                (ImpactSearchBean)AdfFacesContext.getCurrentInstance().getPageFlowScope().get("ImpactSearchBean");
            AdfFacesContext.getCurrentInstance().addPartialTarget(impactSearchBean.getHierarchySourceTree());
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(impactSearchBean.getHierarchySourceTree());
        } else if (multiSearch) {
            AdfFacesContext.getCurrentInstance().addPartialTarget(multiHierarchySourceTree);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(multiHierarchySourceTree);
        } else {
            WhodTermHierarchyBean termHierarchyBean =
                (WhodTermHierarchyBean)AdfFacesContext.getCurrentInstance().getPageFlowScope().get("WhodTermHierarchyBean");
            AdfFacesContext.getCurrentInstance().addPartialTarget(termHierarchyBean.getSourceTree());
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(termHierarchyBean.getSourceTree());
        }

    }


    public void clearTree() {
        CSMQBean.logger.info(userBean.getCaller() + " CREATING SOURCE TREE");
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("SourceTreeVO1Iterator");
        if (dciterb == null)
            return; // theres no data here anyway
        ViewObject smallTreeVO = dciterb.getViewObject();

        smallTreeVO.setNamedWhereClauseParam("dictContentID", CSMQBean.HIERARCHY_KILL_SWITCH);
        smallTreeVO.executeQuery();

        termHierarchySourceBean.init(false);
    }


    private void clearSearch(SelectionEvent selectionEvent) {
        try {
            BindingContext bc = BindingContext.getCurrent();
            DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
            DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("HierarchySourceTermSearchVO1Iterator");
            ViewObject vo = dciterb.getViewObject();

            vo.executeEmptyRowSet();
            nMQSourceTermSearchUIBean =
                    (WhodSourceTermSearchUIBean)ADFContext.getCurrent().getRequestScope().get("WhodSourceTermSearchUIBean");


            resolveMethodExpression("bindings.HierarchySourceTermSearch.collectionModel.makeCurrent}", null,
                                    new Class[] { SelectionEvent.class }, new Object[] { selectionEvent });


            AdfFacesContext.getCurrentInstance().addPartialTarget(nMQSourceTermSearchUIBean.getControlResultsTable());
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(nMQSourceTermSearchUIBean.getControlResultsTable());
        } catch (Exception e) {
        }
    }


    public void updateRelations(ActionEvent actionEvent) {
        int result =
            processUpdateRelations(termHierarchyBean.getTargetTree(), nMQWizardBean.getCurrentDictContentID());
        if (result == 0)
            termHierarchyBean.showStatus(CSMQBean.MQ_SAVED);
        else
            termHierarchyBean.showStatus(CSMQBean.MQ_SAVE_ERROR);
        termHierarchyBean.refresh();
    }

    public DnDAction onTreeDrop(DropEvent dropEvent) {
        termHierarchyBean.showStatus(CSMQBean.MQ_MODIFIED);
        return processDragAndDropEvent(dropEvent, termHierarchyBean.getSourceTree(), termHierarchyBean.getTargetTree(),
                                       termHierarchyBean.getTreemodel(), Integer.parseInt(getParamScope()));
    }

    public void deleteSelected(DialogEvent dialogEvent) {
        //termHierarchyBean.showStatus(CSMQBean.MQ_MODIFIED);
        processDeleteSelected(dialogEvent, termHierarchyBean.getTargetTree(), termHierarchyBean.getTreemodel());
    }
    
    public void processDeleteSelected(DialogEvent dialogEvent, RichTreeTable tree, TreeModel treeModel) {
        int numberOfTermsToBeDeleted = 0;
        List immediateChildren = ((GenericTreeNode)tree.getRowData(0)).getChildren();
        ArrayList<GenericTreeNode> nodesToBeDeleted = new ArrayList<GenericTreeNode>();

        Iterator iterator = immediateChildren.iterator();

        CSMQBean.logger.info(userBean.getCaller() + " ** CREATING LIST FOR DELETE **");

        while (iterator.hasNext()) {
            GenericTreeNode genericTreeNode = (GenericTreeNode)iterator.next();
            if (genericTreeNode.isMarkedForDeletion()) {
                CSMQBean.logger.info(userBean.getCaller() + " ADDING TO DELETE LIST: " + genericTreeNode.toString());
                nodesToBeDeleted.add(genericTreeNode);
                numberOfTermsToBeDeleted++;
            }
        }

        if (dialogEvent.getOutcome().equals(DialogEvent.Outcome.ok)) {
            CSMQBean.logger.info(userBean.getCaller() + " DELETING");
            for (GenericTreeNode gtn : nodesToBeDeleted) {
                
                String sql = "{? = call cqt_whod_ui_tms_utils.DELETE_RELATION_DATA(?,?)}";
                DBTransaction dBTransaction = DMLUtils.getDBTransaction();
                String newDictContentCode = "";
                FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "MQ updated successfully.", null);
                    CallableStatement cstmt = dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT);
                    Connection con;
                try {
                            con = cstmt.getConnection();
                
                            CSMQBean.logger.info(userBean.getCaller() + " ** SAVING RELATIONS");

                            cstmt.registerOutParameter(1, Types.VARCHAR);
                            cstmt.setLong(2, new Long(gtn.getDictRelationId()));
                            cstmt.registerOutParameter(3, Types.VARCHAR);
                            cstmt.execute();
                            newDictContentCode = "" + cstmt.getString(3);
                    con.commit();
                    cstmt.close();
                    BindingContext bc = BindingContext.getCurrent();
                    DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
                    DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("WHODSmallTreeVO1Iterator");
                    ViewObject vo = dciterb.getViewObject();
                    vo.setNamedWhereClauseParam("dictContentID", gtn.getDictContentId());
                    vo.executeQuery();
                    termHierarchyBean.getRoot().getParentNode().getChildren().remove(gtn);
                    //termHierarchyBean.getTreemodel().getParentNode().getChildren().remove(gtn); // GET THE PARRENT AND REMOVE ITSELF
                    RowKeySet rks = new RowKeySetTreeImpl(true);
                    rks.setCollectionModel(treeModel);
                    tree.setDisclosedRowKeys(rks);
                    AdfFacesContext.getCurrentInstance().addPartialTarget(termHierarchyBean.getTargetTree());
                    AdfFacesContext.getCurrentInstance().partialUpdateNotify(termHierarchyBean.getTargetTree());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //  18-JUL-2012  - ADD IT TO THE DELETES ARRAY
               // deletes.put(gtn.getPrikey(), gtn);
            }
        } else {
            CSMQBean.logger.info(userBean.getCaller() + " NOT DELETING");
        }

    }
    
    public void dictionaryTypeChanged(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent == null)
            return;
        String newDictionary = valueChangeEvent.getNewValue().toString();
        if (newDictionary.equals(CSMQBean.WHOD_BASE_DICTIONARY)) {
            this.paramDictionary = nMQWizardBean.getCurrentBaseDictionaryShortName();
            nMQWizardBean.setIsMedDRA(true);
            controlLevel.setValue("ATC1");
            this.showBaseLevelSelItems = true;
            this.showFilterSelItems = false;
        } else {
            this.paramDictionary = nMQWizardBean.getCurrentFilterDictionaryShortName();
            nMQWizardBean.setIsMedDRA(false);
            this.showBaseLevelSelItems = false;
            this.showFilterSelItems = true;
            controlLevel.setValue("CDG1");
        }
        AdfFacesContext.getCurrentInstance().addPartialTarget(getControlLevel());
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(getControlLevel());
        //refreshLevelList(paramDictionary);
    }

    //    public void refreshLevelList (String dictionary) {
    //        nMQSourceTermSearchUIBean = (NMQSourceTermSearchUIBean)ADFContext.getCurrent().getRequestScope().get("NMQSourceTermSearchUIBean");
    //        this.paramDictionaryType = getControlDictionaryType().getValue().toString();
    //
    //        BindingContext bc = BindingContext.getCurrent();
    //        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
    //        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("LevelsByDictionaryVO1Iterator");
    //        ViewObject vo = dciterb.getViewObject();
    //
    //        vo.setNamedWhereClauseParam("dictShortName", dictionary);
    //        vo.executeQuery();
    //
    //        AdfFacesContext.getCurrentInstance().addPartialTarget(getControlLevel());
    //        AdfFacesContext.getCurrentInstance().partialUpdateNotify(getControlLevel());
    //    }


    public void dialogClosed(PopupCanceledEvent popupCanceledEvent) {
        clearResults();
        nMQSourceTermSearchUIBean =
                (WhodSourceTermSearchUIBean)ADFContext.getCurrent().getRequestScope().get("WhodSourceTermSearchUIBean");
        if (nMQSourceTermSearchUIBean.getCntrlScope() == null || getControlDictionaryType().getValue() == null)
            return;
        if (getControlDictionaryType().getValue().toString().equals("BASE"))
            nMQSourceTermSearchUIBean.getCntrlScope().setDisabled(true);
        else
            nMQSourceTermSearchUIBean.getCntrlScope().setDisabled(false);

        AdfFacesContext.getCurrentInstance().addPartialTarget(nMQSourceTermSearchUIBean.getCntrlScope());
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(nMQSourceTermSearchUIBean.getCntrlScope());
    }


    private void clearResults() {
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("HierarchySourceTermSearchVO1Iterator");
        ViewObject search = dciterb.getViewObject();

        // CLEAR THE RESULTS
        search.setNamedWhereClauseParam("killSwitch", CSMQBean.KILL_SWITCH_ON);
        search.executeQuery();

        nMQSourceTermSearchUIBean =
                (WhodSourceTermSearchUIBean)ADFContext.getCurrent().getRequestScope().get("WhodSourceTermSearchUIBean");
        nMQSourceTermSearchUIBean.getControlResultsTable().clearLocalCache();

        AdfFacesContext.getCurrentInstance().addPartialTarget(nMQSourceTermSearchUIBean.getControlResultsTable());
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(nMQSourceTermSearchUIBean.getControlResultsTable());
    }

    public void showSecondaryPathChanged(ValueChangeEvent valueChangeEvent) {
        if (valueChangeEvent.getNewValue().toString().equals(valueChangeEvent.getOldValue()))
            return; // it didn't change
        refreshTree();
    }

    public void setParamScope(String paramScope) {
        this.paramScope = paramScope;
    }

    public String getParamScope() {
        nMQSourceTermSearchUIBean =
                (WhodSourceTermSearchUIBean)ADFContext.getCurrent().getRequestScope().get("WhodSourceTermSearchUIBean");
        if (nMQSourceTermSearchUIBean != null && nMQSourceTermSearchUIBean.getCntrlScope() != null)
            this.paramScope = nMQSourceTermSearchUIBean.getCntrlScope().getValue().toString();
        return paramScope;
    }

    public void setParamSort(String sort) {
        this.paramSort = sort;
    }

    public String getParamSort() {
        nMQSourceTermSearchUIBean =
                (WhodSourceTermSearchUIBean)ADFContext.getCurrent().getRequestScope().get("WhodSourceTermSearchUIBean");
        if (nMQSourceTermSearchUIBean != null && nMQSourceTermSearchUIBean.getCntrlSortList() != null)
            this.paramSort = nMQSourceTermSearchUIBean.getCntrlSortList().getValue().toString();
        return paramSort;
    }

    public void refresh(ActionEvent actionEvent) {
        refreshTree();
    }

    public void sortChanged(ValueChangeEvent valueChangeEvent) {
        this.paramSort = valueChangeEvent.getNewValue().toString();
    }

    public void scopeChanged(ValueChangeEvent valueChangeEvent) {
        this.paramScope = valueChangeEvent.getNewValue().toString();
    }

    public void setParamNarrowScopeOnly(String paramNarrowScopeOnly) {
        this.paramNarrowScopeOnly = paramNarrowScopeOnly;
    }

    public String getParamNarrowScopeOnly() {
        if (nMQSourceTermSearchUIBean != null && nMQSourceTermSearchUIBean.getCntrlNarrowScope() != null &&
            nMQSourceTermSearchUIBean.getCntrlNarrowScope().getValue() != null)
            this.paramNarrowScopeOnly =
                    nMQSourceTermSearchUIBean.getCntrlNarrowScope().getValue().toString().equals("true") ?
                    CSMQBean.TRUE : CSMQBean.FALSE;
        return paramNarrowScopeOnly;
    }

    public void setParamPrimLinkFlag(String returnPrimLinkFlag) {
        this.paramPrimLinkFlag = returnPrimLinkFlag;
    }

    public String getParamPrimLinkFlag() {
        if (nMQSourceTermSearchUIBean != null && nMQSourceTermSearchUIBean.getCtrlReturnPrimLinkPath() != null)
            this.paramPrimLinkFlag =
                    nMQSourceTermSearchUIBean.getCtrlReturnPrimLinkPath().getValue().toString().equals("true") ?
                    CSMQBean.TRUE : CSMQBean.FALSE;
        return paramPrimLinkFlag;
    }


    public void nodeChanged(ValueChangeEvent valueChangeEvent) {


        /* soc3 = scope
         * soc2 = category
         * ot6 = weight
         */
        WhodTermHierarchyBean termHierarchyBean =
            (WhodTermHierarchyBean)AdfFacesContext.getCurrentInstance().getPageFlowScope().get("WhodTermHierarchyBean");

        Object o = valueChangeEvent;
        RowKeySet rks = termHierarchyBean.getTargetTree().getSelectedRowKeys();
        Iterator rksIterator = rks.iterator();
        List key = (List)rksIterator.next();

        int rowKey = Integer.parseInt(key.get(1).toString());
        GenericTreeNode node = (GenericTreeNode)termHierarchyBean.getTargetTree().getRowData(rowKey);

        // TEST TO SEE IF IT'S ALREADY BEEN EDITED
        GenericTreeNode oldNode = updates.get(node.getPrikey());
        if (oldNode != null)
            node = oldNode;

        // UPDATE THE NODE (EITHER THE OLD OR THE NEW) WITH THE CHANGED VALUES
        if (valueChangeEvent.getSource().toString().indexOf("soc3") > -1)
            node.setFormattedScope(valueChangeEvent.getNewValue().toString());

        if (valueChangeEvent.getSource().toString().indexOf("soc2") > -1)
            node.setTermCategory(valueChangeEvent.getNewValue().toString());

        if (valueChangeEvent.getSource().toString().indexOf("ot6") > -1)
            node.setTermWeight(valueChangeEvent.getNewValue().toString());

        if (oldNode == null) // ADD IT SINCE IT'S NOT THERE ALREADY
            updates.put(node.getPrikey(), node);

        termHierarchyBean.showStatus(CSMQBean.MQ_MODIFIED);
    }


    public void searchPopupLoad(PopupFetchEvent popupFetchEvent) {

        // init level list

        //refreshLevelList(nMQWizardBean.getCurrentFilterDictionaryShortName());

        //dictionaryTypeChanged(null);
    }

    public void setParamExtension(String paramExtension) {
        this.paramExtension = paramExtension;
    }

    public String getParamExtension() {
        return paramExtension;
    }

    public void setControlDictionaryType(RichSelectOneChoice controlDictionaryType) {
        this.controlDictionaryType = controlDictionaryType;
    }

    public RichSelectOneChoice getControlDictionaryType() {
        return controlDictionaryType;
    }

    public void setControlLevel(RichSelectOneChoice controlLevel) {
        this.controlLevel = controlLevel;
    }

    public RichSelectOneChoice getControlLevel() {
        return controlLevel;
    }


    public void setShowBaseLevelSelItems(boolean showMedDRASelItems) {
        this.showBaseLevelSelItems = showMedDRASelItems;
    }

    public boolean isShowBaseLevelSelItems() {
        return showBaseLevelSelItems;
    }

    public void setShowFilterSelItems(boolean showNMQSelItems) {
        this.showFilterSelItems = showNMQSelItems;
    }

    public boolean isShowFilterSelItems() {
        return showFilterSelItems;
    }

    public DnDAction onMultiTreeDrop(DropEvent dropEvent) {
        RichTreeTable source = multiHierarchySourceTree;
        if (dropEvent.getDragClientId().equals("pt1:t5"))
            source = controlMultiResultsTable;

        termHierarchyBean.showStatus(CSMQBean.MQ_MODIFIED);
        ///  THIS WILL PROBABLY NEED TO BE FIXED TO USE THE OTHER TREES - TES 1/26/2012
        return processDragAndDropEvent(dropEvent, source, termHierarchyBean.getTargetTree(),
                                       termHierarchyBean.getTreemodel(), Integer.parseInt(getParamScope()));
    }


    public void setMultiHierarchySourceTree(RichTreeTable multiHierarchySourceTree) {
        this.multiHierarchySourceTree = multiHierarchySourceTree;
    }

    public RichTreeTable getMultiHierarchySourceTree() {
        return multiHierarchySourceTree;
    }

    public void setControlMultiResultsTable(RichTreeTable controlMultiResultsTable) {
        this.controlMultiResultsTable = controlMultiResultsTable;
    }

    public RichTreeTable getControlMultiResultsTable() {
        return controlMultiResultsTable;
    }

    public void loadHierachy(ActionEvent actionEvent) {
        multiSelectRowChanged(null);
    }

    public int processUpdateRelations(RichTreeTable tree, String dictContentID) {
        /*
        FUNCTION insert_relation_data
                (pDictContentID        IN NUMBER,
                 pDictContentRefID     IN NUMBER,
                 pNamedRelName         IN VARCHAR2  DEFAULT NULL,
                 pCommentText          IN VARCHAR2  DEFAULT NULL,
                 pStatus               IN VARCHAR2  DEFAULT NULL,
                 pPredictRelationID   OUT NUMBER)
              RETURN VARCHAR2;
        */
        String currentDictContentID = dictContentID;
        String currentPredictGroups = this.defaultDraftGroupName;
        String currentUser = userBean.getUsername();
        Object[][] relations = getChildrenForUpdate();
        String DML[] = { "I", "U", "D" };

        String sql = "{? = call cqt_whod_ui_tms_utils.insert_relation_data(?,?,?,?,?  ,?)}";
        DBTransaction dBTransaction = DMLUtils.getDBTransaction();
        String newDictContentCode = "";
        FacesMessage msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "MQ updated successfully.", null);
        try {
            CallableStatement cstmt = dBTransaction.createCallableStatement(sql, DBTransaction.DEFAULT);
            Connection con = cstmt.getConnection();
            CSMQBean.logger.info(userBean.getCaller() + " ** SAVING RELATIONS");
            CSMQBean.logger.info(userBean.getCaller() + " currentDictContentID: " + currentDictContentID);
            CSMQBean.logger.info(userBean.getCaller() + " currentPredictGroups: " + currentPredictGroups);
            CSMQBean.logger.info(userBean.getCaller() + " currentUser: " + currentUser);

            for (Object[] relation : relations) {
                if (DML[0].equals(relation[VAL_DML])) {
                    cstmt.registerOutParameter(1, Types.VARCHAR);
                    cstmt.setLong(2, new Long(currentDictContentID));
                    cstmt.setLong(3, new Long((String)relation[VAL_DICT_CONTENT_ID]));
                    cstmt.setString(4, null);
                    cstmt.setString(5, null);
                    cstmt.setString(6, null);
                    cstmt.registerOutParameter(7, Types.INTEGER);
                    cstmt.executeUpdate();
                    newDictContentCode = "" + cstmt.getInt(7);
                }
            }
            con.commit();
            cstmt.close();

            // REMOVE ALL THE PENDING CHANGES - JUST TO BE SAFE
            inserts.clear();
            updates.clear();
            deletes.clear();

        } catch (SQLException e) {
            String disMsg = "The following error occurred: " + e.getMessage();
            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, disMsg, null);
            FacesContext.getCurrentInstance().addMessage(null, msg);
            e.printStackTrace();
            return -1;
        }

        return 0;
    }
}
