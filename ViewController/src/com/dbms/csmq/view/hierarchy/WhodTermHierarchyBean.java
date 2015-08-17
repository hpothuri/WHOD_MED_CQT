package com.dbms.csmq.view.hierarchy;


import com.dbms.csmq.CSMQBean;
import com.dbms.csmq.view.backing.whod.WhodUtils;
import com.dbms.csmq.view.backing.whod.WhodWizardBean;
import com.dbms.util.Utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.view.rich.component.rich.data.RichTreeTable;
import oracle.adf.view.rich.component.rich.input.RichInputText;
import oracle.adf.view.rich.component.rich.input.RichSelectOneChoice;
import oracle.adf.view.rich.component.rich.layout.RichToolbar;
import oracle.adf.view.rich.component.rich.output.RichImage;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.model.ChildPropertyTreeModel;
import org.apache.myfaces.trinidad.model.ModelUtils;
import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetTreeImpl;
import org.apache.myfaces.trinidad.model.TreeModel;


public class WhodTermHierarchyBean extends Hierarchy {
    private TreeModel treemodel;
    //private GenericTreeNode root;
    private Enumeration rows;
    private HashMap parentNodesByLevel;
    private boolean editable;
    private String queryLevel;

    //Search terms used for term list box
    private String dictionaryVersionSearchCriteria = "";
    private String levelSearchCriteria = "";
    private String termSearchCriteria = "";
    private RichSelectOneChoice dictionaryVersion;
    private RichSelectOneChoice levelList;
    private RichInputText term;

    private String currentDictionary;
    private boolean hasScope = false;
    WhodWizardBean nMQWizardBean;
    private RichTreeTable targetTree;
    private RichTreeTable sourceTree;
    String ignorePredict;
    private boolean exportAllowed = true;


    public WhodTermHierarchyBean() {
        nMQWizardBean = (WhodWizardBean)AdfFacesContext.getCurrentInstance().getPageFlowScope().get("WhodWizardBean");
    }

    public void init(boolean hasScope) {
        this.hasScope = hasScope;
        ignorePredict = nMQWizardBean.getCurrentStatus().equals("CURRENT") ? CSMQBean.TRUE : CSMQBean.FALSE;
        parentNodesByLevel = new HashMap();
        this.currentDictionary = nMQWizardBean.getCurrentFilterDictionaryShortName();
        if (!createTree()) { // there is no data (or a clear was called) - bail
            treemodel = new ChildPropertyTreeModel() {
                    public boolean isContainer() {
                        return false;
                    }
                };
            return;
        }
        List nodes = new ArrayList();
        nodes.add(root);
        treemodel = new ChildPropertyTreeModel(nodes, "children") {
                public boolean isContainer() {
                    if (getRowData() == null)
                        return false;
                    return ((GenericTreeNode)getRowData()).getChildCount() > 0;
                }
            };
    }

    private RichToolbar cntrlStatusBar;
    private RichImage iconMQChanged;
    private RichImage iconMQSaveError;
    private RichImage iconMQSaved;

    public void showStatus(int code) {
        try {
            this.iconMQChanged.setVisible(false);
            this.iconMQSaveError.setVisible(false);
            this.iconMQSaved.setVisible(false);

            switch (code) {
            case CSMQBean.MQ_SAVED:
                this.iconMQSaved.setVisible(true);
                this.setExportAllowed(true);
                break;
            case CSMQBean.MQ_SAVE_ERROR:
                this.iconMQSaveError.setVisible(true);
                this.setExportAllowed(false);
                break;
            case CSMQBean.MQ_MODIFIED:
                this.iconMQChanged.setVisible(true);
                this.setExportAllowed(false);
                break;
            }
            AdfFacesContext.getCurrentInstance().addPartialTarget(cntrlStatusBar);
            AdfFacesContext.getCurrentInstance().partialUpdateNotify(cntrlStatusBar);
        } catch (java.lang.NullPointerException e) {
        } //ignore it
    }

    private boolean createTree() {
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("WHODSmallTreeVO1Iterator");
        rows = dciterb.getRowSetIterator().enumerateRowsInRange();
        if (!rows.hasMoreElements()) {
            formRootNodeIfNoData();
            return true; // there's no data - bail
        }
        Row row = (Row)rows.nextElement();
        root = new GenericTreeNode();
        root.setIsRoot(true);
        root.setTerm(Utils.getAsString(row, "ParentTerm"));
        root.setParent("0");
        root.setLevelName(resolveTermLevel(Utils.getAsString(row, "ParentLevel")));
        root.setQueryLevel(Utils.getAsString(row, "ParentLevel"));
        root.setLevel(Utils.getAsNumber(row, "ParentLevel"));
        root.setDictShortName(Utils.getAsString(row, "DictShortName"));
        root.setDictContentId(Utils.getAsString(row, "ParentDictContentId"));
        root.setDictContentCode(Utils.getAsString(row, "ParentDictContentCode"));
        root.setApprovedFlag(Utils.getAsString(row, "ApprovedFlag"));
        root.setStatus(Utils.getAsString(row, "Status"));
        // set it as expanded so that it won't get called again
        root.setIsExpanded(true);
        populateTreeNodes(root, row);
        //clean up the hashmap
        parentNodesByLevel = null;
        return true;
    }

    private void formRootNodeIfNoData() {
        WhodWizardBean whodWizardBean = WhodUtils.getWhodWizardBean();
        root = new GenericTreeNode();
        root.setIsRoot(true);
        root.setTerm(whodWizardBean.getCurrentTermName());
        root.setParent("0");
        root.setLevelName(whodWizardBean.getCurrentTermLevel());
        root.setQueryLevel(whodWizardBean.getCurrentTermLevel());
        //root.setLevel(Utils.getAsNumber(row, "ParentLevel"));
        root.setDictShortName(whodWizardBean.getActiveDictionary());
        root.setDictContentId(whodWizardBean.getCurrentDictContentID());
        root.setDictContentCode(whodWizardBean.getCurrentContentCode());
        root.setApprovedFlag(whodWizardBean.getCurrentActivationBy());
        root.setStatus(whodWizardBean.getCurrentStatus());
        // set it as expanded so that it won't get called again
        root.setIsExpanded(true);
        root.setEditable(true);
        parentNodesByLevel = null;
    }

    private GenericTreeNode populateTreeNodes(GenericTreeNode node, Row row) {
        //store node and level
        if (parentNodesByLevel == null)
            return null;
        parentNodesByLevel.put(node.getDictContentId(), node);
        GenericTreeNode termNode = new GenericTreeNode();
        termNode.setTerm(Utils.getAsString(row, "ChildTerm"));
        termNode.setParent(Utils.getAsString(row, "ParentDictContentId"));
        //termNode.setLevelName(resolveTermLevel(Utils.getAsString(row, "ChildLevel")));
        termNode.setQueryLevel(Utils.getAsString(row, "ChildLevel"));
        termNode.setLevel(Utils.getAsNumber(row, "ChildLevel"));
        termNode.setDictShortName(Utils.getAsString(row, "DictShortName"));
        termNode.setDictContentId(Utils.getAsString(row, "ChildDictContentId"));
        termNode.setDictContentCode(Utils.getAsString(row, "ChildDictContentCode"));
        termNode.setApprovedFlag(Utils.getAsString(row, "ApprovedFlag"));
        termNode.setStatus(Utils.getAsString(row, "Status"));

        boolean showMoreChildren = Utils.getAsBoolean(row, "ChildExists");
        if (showMoreChildren) {
            termNode.setShowHasChildrenButton(true);
        }
        termNode.setHasScope(this.hasScope);
        termNode.setEditable(this.editable);
        //termNode.setFormattedScope(Utils.getAsString(row, "FormattedScope"));
        String childLevel = Utils.getAsString(row, "ChildLevel");
        String subLevelRefName = Utils.getAsString(row, "LevelRefName");
        String namedRelation = Utils.getAsString(row, "NamedRelation");
        //        CSMQBean.logger.info(userBean.getCaller() + " subLevelRefName: " + subLevelRefName + ";;; childLevel=" +
        //                             childLevel + ";;; namedRelation: " + namedRelation);
        if (subLevelRefName != null && subLevelRefName.contains("ATC") || subLevelRefName.contains("PT")) {
            if (namedRelation != null) {
                if (namedRelation.contains("BROAD")) {
                    termNode.setFormattedScope("1");
                } else if (namedRelation.contains("NARROW")) {
                    termNode.setFormattedScope("2");
                } else if (namedRelation.contains("CHILD")) {
                    termNode.setFormattedScope("3");
                }
            }
            termNode.setShowHasChildrenButton(false);
            termNode.setLevelName(subLevelRefName);
        } else {
            termNode.setLevelName(childLevel);
        }

        //        CSMQBean.logger.info(userBean.getCaller() + " parentNodesByLevel: " + parentNodesByLevel +
        //                             ":; termNode.getParent()=" + termNode.getParent());
        GenericTreeNode parentNode = (GenericTreeNode)parentNodesByLevel.get(termNode.getParent());
        if (parentNode != null) {
            parentNode.getChildren().add(termNode); // add to the parent
            termNode.setParentNode(parentNode); // set the parent for the child
            if (root.equals(parentNode))
                termNode.setIsDirectRelation(true); // it's a direct relation
            if (parentNode.isIsRoot())
                termNode.setDeletable(true); //it's a child of the root - it can be deleted
            //            CSMQBean.logger.info(userBean.getCaller() + " ADDING NODE: " + termNode);
            setDerivedRelations(termNode);
            if (rows.hasMoreElements()) {
                Row nextRow = (Row)rows.nextElement();
                populateTreeNodes(termNode, nextRow);
            }
        }

        return node;
    }

    private boolean createTreeOld() {
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("WHODSmallTreeVO1Iterator");
        rows = dciterb.getRowSetIterator().enumerateRowsInRange();
        if (!rows.hasMoreElements())
            return false; // there's no data - bail

        Row row = (Row)rows.nextElement();
        root = new GenericTreeNode();
        root.setIsRoot(true);
        root.setTerm(Utils.getAsString(row, "Term"));
        root.setPrikey(Utils.getAsString(row, "Prikey"));
        root.setParent(Utils.getAsString(row, "Parent"));
        //root.setLevelName(Utils.getAsString(row,"LevelName"));
        root.setLevelName(resolveTermLevel(Utils.getAsString(row, "LevelName")));
        root.setQueryLevel(Utils.getAsString(row, "LevelName"));
        //root.setQueryLevel(Utils.getAsString(row,"QueryLevel"));
        root.setLevel(Utils.getAsNumber(row, "Level"));
        root.setDictShortName(Utils.getAsString(row, "DictShortName"));
        root.setDictContentId(Utils.getAsString(row, "DictContentId"));
        root.setDictContentCode(Utils.getAsString(row, "DictContentCode"));
        root.setApprovedFlag(Utils.getAsString(row, "ApprovedFlag"));
        root.setDictContentAltCode(Utils.getAsString(row, "DictContentAltCode"));
        root.setStatus(Utils.getAsString(row, "Status"));
        root.setPredictGroupId(Utils.getAsNumber(row, "PredictGroupId"));
        root.setHasScope(hasScope);
        root.setTermCategory(Utils.getAsString(row, "Termcat"));
        root.setTermLevel(Utils.getAsString(row, "Termlvl"));
        root.setTermScope(Utils.getAsNumber(row, "Termscp"));
        root.setTermWeight(Utils.getAsString(row, "Termweig"));
        root.setPath(Utils.getAsString(row, "TermPath"));
        root.setFormattedScope(Utils.getAsString(row, "FormattedScope"));
        //AMC queryLevel
        Pattern pattern = Pattern.compile("[0-9]");
        Matcher matcher = pattern.matcher(root.getLevelName());
        if (matcher.find()) {
            queryLevel = matcher.group();
        }

        /*
        if (root.getLevelName().contains (CSMQBean.NMQ)) {
            root.setMqType(CSMQBean.NMQ);
            root.setEditable(true);
            this.editable = true;
            }
        else {
            root.setMqType(CSMQBean.SMQ);
            root.setEditable(false);
            this.editable = false;
            }
        */
        // CHANGE TO LOOK AT EXTENSION INSTEAD OF LEVEL
        if (nMQWizardBean.getCurrentExtension().equals(CSMQBean.SMQ)) {
            root.setMqType(CSMQBean.SMQ);
            root.setEditable(false);
            this.editable = false;
        } else {
            root.setMqType(CSMQBean.NMQ);
            root.setEditable(true);
            this.editable = true;
        }
        // set it as expanded so that it won't get called again
        root.setIsExpanded(true);
        populateTreeNodesOld(root);
        //clean up the hashmap
        parentNodesByLevel = null;
        return true;
    }

    private GenericTreeNode populateTreeNodesOld(GenericTreeNode node) {
        //store node and level
        if (parentNodesByLevel == null)
            return null;
        parentNodesByLevel.put(node.getDictContentId(), node);

        if (rows.hasMoreElements()) {
            Row row = (Row)rows.nextElement();
            GenericTreeNode termNode = new GenericTreeNode();
            termNode.setTerm(Utils.getAsString(row, "Term"));
            //termNode.setPrikey(Utils.getAsString(row, "Prikey"));
            termNode.setParent(Utils.getAsString(row, "Parent"));
            termNode.setLevelName(resolveTermLevel(Utils.getAsString(row, "LevelName")));
            termNode.setQueryLevel(Utils.getAsString(row, "LevelName"));
            termNode.setLevel(Utils.getAsNumber(row, "Level"));
            termNode.setDictShortName(Utils.getAsString(row, "DictShortName"));
            termNode.setDictContentId(Utils.getAsString(row, "DictContentId"));
            termNode.setDictContentCode(Utils.getAsString(row, "DictContentCode"));
            termNode.setApprovedFlag(Utils.getAsString(row, "ApprovedFlag"));
            termNode.setDictContentAltCode(Utils.getAsString(row, "DictContentAltCode"));
            termNode.setStatus(Utils.getAsString(row, "Status"));
            termNode.setPredictGroupId(Utils.getAsNumber(row, "PredictGroupId"));
            termNode.setPath(Utils.getAsString(row, "TermPath"));
            termNode.setPrimaryLink(Utils.getAsBoolean(row, "PrimLinkFlag"));

            if (!termNode.isPrimaryLink())
                termNode.setStyle("SECONDARY_LINK");

            // FOR 'LAZY' LOADING
            //String hasChildren = Utils.getAsString(row,"ChildExists");
            //boolean showMoreChildren = hasChildren.equals("Y") ? true : false;

            boolean showMoreChildren = Utils.getAsBoolean(row, "ChildExists");
            if (showMoreChildren) {
                //termNode.setIcon(CSMQBean.getProperty("HAS_CHILDREN_ICON"));
                termNode.setShowHasChildrenButton(true);
            }
            termNode.setTermCategory(Utils.getAsString(row, "Termcat"));
            termNode.setTermLevel(Utils.getAsString(row, "Termlvl"));
            termNode.setTermScope(Utils.getAsNumber(row, "Termscp"));
            termNode.setTermWeight(Utils.getAsString(row, "Termweig"));
            termNode.setHasScope(this.hasScope);
            termNode.setEditable(this.editable);
            termNode.setFormattedScope(Utils.getAsString(row, "FormattedScope"));
            CSMQBean.logger.info(userBean.getCaller() + " parentNodesByLevel: " + parentNodesByLevel +
                                 ":; termNode.getParent()=" + termNode.getParent());
            GenericTreeNode parentNode = (GenericTreeNode)parentNodesByLevel.get(termNode.getParent());
            if (parentNode != null) {
                parentNode.getChildren().add(termNode); // add to the parent
                termNode.setParentNode(parentNode); // set the parent for the child
                if (root.equals(parentNode))
                    termNode.setIsDirectRelation(true); // it's a direct relation
                if (parentNode.isIsRoot())
                    termNode.setDeletable(true); //it's a child of the root - it can be deleted
                CSMQBean.logger.info(userBean.getCaller() + " ADDING NODE: " + termNode);
                setDerivedRelations(termNode);
                populateTreeNodesOld(termNode);

            }
        }
        return node;
    }


    private void showChildren() {
        clearKeys(targetTree);
        GenericTreeNode newRootNode = null;
        RichTreeTable tree = targetTree;
        RowKeySet droppedValue = targetTree.getSelectedRowKeys();
        Object[] keys = droppedValue.toArray();
        for (int i = 0; i < keys.length; i++) {
            List list = (List)keys[i];

            int depth = list.size();
            int rootKey = Integer.parseInt(list.get(0).toString());
            GenericTreeNode c1 = null;
            GenericTreeNode c2 = null;
            GenericTreeNode c3 = null;
            GenericTreeNode c4 = null;

            int c1key;
            int c2key;
            int c3key;
            int c4key;

            switch (depth) {

            case 1:
                newRootNode = root;
                break;
            case 2:
                c1key = Integer.parseInt(list.get(1).toString());
                c1 = (GenericTreeNode)root.getChildren().get(c1key);
                newRootNode = c1;
                break;
            case 3:
                c1key = Integer.parseInt(list.get(1).toString());
                c1 = (GenericTreeNode)root.getChildren().get(c1key);
                c2key = Integer.parseInt(list.get(2).toString());
                c2 = (GenericTreeNode)c1.getChildren().get(c2key);
                newRootNode = c2;
                break;
            case 4:
                c1key = Integer.parseInt(list.get(1).toString());
                c1 = (GenericTreeNode)root.getChildren().get(c1key);
                c2key = Integer.parseInt(list.get(2).toString());
                c2 = (GenericTreeNode)c1.getChildren().get(c2key);
                c3key = Integer.parseInt(list.get(3).toString());
                c3 = (GenericTreeNode)c2.getChildren().get(c3key);
                newRootNode = c3;
                break;
            case 5:
                c1key = Integer.parseInt(list.get(1).toString());
                c1 = (GenericTreeNode)root.getChildren().get(c1key);
                c2key = Integer.parseInt(list.get(2).toString());
                c2 = (GenericTreeNode)c1.getChildren().get(c2key);
                c3key = Integer.parseInt(list.get(3).toString());
                c3 = (GenericTreeNode)c2.getChildren().get(c3key);
                c4key = Integer.parseInt(list.get(4).toString());
                c4 = (GenericTreeNode)c3.getChildren().get(c4key);
                newRootNode = c4;
                break;
            }
        }

        if (newRootNode.isIsExpanded())
            return; // don't requery if already done

        // REQUERY AND GET THE CHILDREN

        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("WHODSmallTreeVO1Iterator");
        ViewObject vo = dciterb.getViewObject();
        String parentTermScope = "0";
        if (newRootNode != null)
            parentTermScope = newRootNode.getFormattedScope();

        vo.setNamedWhereClauseParam("dictContentID", newRootNode.getDictContentId());
        CSMQBean.logger.info(userBean.getCaller() + " dictContentID: " + newRootNode.getDictContentId());
        vo.executeQuery();
        rows = dciterb.getRowSetIterator().enumerateRowsInRange();

        // skip the first row, since it is the parent
        Row row = (Row)rows.nextElement();

        parentNodesByLevel = new HashMap();
        populateTreeNodes(newRootNode, row);

        //newRootNode.setIcon(null); // get rid of the icon
        newRootNode.setIsExpanded(true); // prevent it from being called again
        newRootNode.setShowHasChildrenButton(false);

        RowKeySet rks = new RowKeySetTreeImpl(true);
        rks.setCollectionModel(treemodel);
        tree.setDisclosedRowKeys(rks);

        AdfFacesContext.getCurrentInstance().addPartialTarget(tree);
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(tree);
    }


    private DCIteratorBinding resolveTargetIterWithSpel(String spelExpr) {
        FacesContext fctx = FacesContext.getCurrentInstance();
        ELContext elctx = fctx.getELContext();
        ExpressionFactory elFactory = fctx.getApplication().getExpressionFactory();
        ValueExpression valueExpr = elFactory.createValueExpression(elctx, spelExpr, Object.class);
        DCIteratorBinding dciter = (DCIteratorBinding)valueExpr.getValue(elctx);
        return dciter;
    }


    public void setTreemodel(TreeModel treemodel) {
        this.treemodel = treemodel;
    }

    public TreeModel getTreemodel() {
        return treemodel;
    }


    public String getDictionaryVersionSearchCriteria() {
        dictionaryVersionSearchCriteria = String.valueOf(getDictionaryVersion().getValue()).trim();
        return dictionaryVersionSearchCriteria;
    }

    public String getLevelSearchCriteria() {
        levelSearchCriteria = String.valueOf(getLevelList().getValue()).trim();
        return levelSearchCriteria;
    }

    public String getTermSearchCriteria() {
        termSearchCriteria = String.valueOf(getTerm().getValue()).trim();
        return termSearchCriteria;
    }


    public void setDictionaryVersion(RichSelectOneChoice dictionaryVersion) {
        this.dictionaryVersion = dictionaryVersion;
    }

    public RichSelectOneChoice getDictionaryVersion() {
        return dictionaryVersion;
    }

    public void setLevelList(RichSelectOneChoice levelList) {
        this.levelList = levelList;
    }

    public RichSelectOneChoice getLevelList() {
        return levelList;
    }

    public void setTerm(RichInputText term) {
        this.term = term;
    }

    public RichInputText getTerm() {
        return term;
    }


    public void setTargetTree(RichTreeTable targetTree) {
        this.targetTree = targetTree;
    }

    public RichTreeTable getTargetTree() {
        return targetTree;
    }

    public void setSourceTree(RichTreeTable sourceTree) {
        this.sourceTree = sourceTree;
    }

    public RichTreeTable getSourceTree() {
        return sourceTree;
    }

    public void setCurrentDictionary(String currentDictionary) {
        this.currentDictionary = currentDictionary;
    }

    public String getCurrentDictionary() {
        return currentDictionary;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setHasScope(boolean hasScope) {
        this.hasScope = hasScope;
    }

    public boolean isHasScope() {
        return hasScope;
    }

    public void expandChildren(ActionEvent actionEvent) {
        try {
            showChildren();
        } catch (Exception e) {
            // TODO: Add catch code
            e.printStackTrace();
            System.out.println("Exception in expandChildren() is " + e);
        }

    }


    public void refresh() {
        this.refresh(null, root.getDictContentId());
    }

    public void refresh(ActionEvent actionEvent) {
        refresh(actionEvent, root.getDictContentId());
        showStatus(CSMQBean.MQ_REFRESHED);
    }

    public void refresh(ActionEvent actionEvent, String rootNode) {

        clearKeys(targetTree);
        clearKeys(sourceTree);

        // REQUERY
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("WHODSmallTreeVO1Iterator");
        ViewObject vo = dciterb.getViewObject();
        vo.setNamedWhereClauseParam("dictContentID", rootNode);
        vo.executeQuery();
        init(this.hasScope);

        //expand the tree
        RowKeySet disclosedRowKeys = new RowKeySetTreeImpl(true);
        disclosedRowKeys.setCollectionModel(ModelUtils.toTreeModel(targetTree.getValue()));
        targetTree.setDisclosedRowKeys(disclosedRowKeys);
        AdfFacesContext.getCurrentInstance().addPartialTarget(targetTree);
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(targetTree);
    }

    public void clear() {
        refresh(null, "0");
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

    public String getQueryLevel() {
        int p = queryLevel.indexOf("Q");
        return queryLevel.substring(p);
    }


    private String resolveTermLevel(String level) {

        Pattern pattern = Pattern.compile("[0-9]");
        Matcher matcher = pattern.matcher(level);
        if (matcher.find())
            return (matcher.group());
        return level;
    }

    public void setExportAllowed(boolean exportAllowed) {
        this.exportAllowed = exportAllowed;
    }

    public boolean isExportAllowed() {
        return exportAllowed;
    }
}
