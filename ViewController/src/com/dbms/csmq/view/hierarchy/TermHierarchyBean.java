package com.dbms.csmq.view.hierarchy;


import com.dbms.csmq.CSMQBean;
import com.dbms.csmq.UserBean;
import com.dbms.csmq.view.backing.NMQ.NMQWizardBean;

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
import oracle.adf.view.rich.component.rich.output.RichOutputFormatted;
import oracle.adf.view.rich.context.AdfFacesContext;

import oracle.jbo.Row;
import oracle.jbo.ViewObject;

import org.apache.myfaces.trinidad.model.ChildPropertyTreeModel;
import org.apache.myfaces.trinidad.model.ModelUtils;
import org.apache.myfaces.trinidad.model.RowKeySet;
import org.apache.myfaces.trinidad.model.RowKeySetTreeImpl;
import org.apache.myfaces.trinidad.model.TreeModel;


public class TermHierarchyBean extends Hierarchy {

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
    NMQWizardBean nMQWizardBean;
    private RichTreeTable targetTree;
    private RichTreeTable sourceTree;
    String ignorePredict;
    private boolean exportAllowed = true;
    

    public TermHierarchyBean () {
        nMQWizardBean = (NMQWizardBean) AdfFacesContext.getCurrentInstance().getPageFlowScope().get("NMQWizardBean");
        }
    
    
    public void init (boolean hasScope) {
        this.hasScope = hasScope;
        ignorePredict =  nMQWizardBean.getCurrentStatus().equals("CURRENT") ? CSMQBean.TRUE : CSMQBean.FALSE;
        
        
        parentNodesByLevel = new HashMap();
        this.currentDictionary = nMQWizardBean.getCurrentFilterDictionaryShortName();
        
        if (!createTree()) {  // there is no data (or a clear was called) - bail
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
                    if (getRowData() == null) return false;
                    return ((GenericTreeNode)getRowData()).getChildCount() > 0;
                }
            }; 
        }
    
    private RichToolbar cntrlStatusBar;

    private RichImage iconMQChanged;
    private RichImage iconMQSaveError;
    private RichImage iconMQSaved;
    
    
    
    public void showStatus (int code) {
    
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
        }
        catch (java.lang.NullPointerException e) {} //ignore it
        
        }
    
    
    private boolean createTree() {
        
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("SmallTreeVO1Iterator");
        
        rows = dciterb.getRowSetIterator().enumerateRowsInRange();
        if (!rows.hasMoreElements()) return false; // there's no data - bail
               
        Row row = (Row)rows.nextElement();

        root = new GenericTreeNode();
        root.setIsRoot(true);
        root.setTerm(Utils.getAsString(row,"Term"));
        
        root.setPrikey(Utils.getAsString(row,"Prikey"));
        root.setParent(Utils.getAsString(row,"Parent"));
        
        //root.setLevelName(Utils.getAsString(row,"LevelName"));
        root.setLevelName(resolveTermLevel(Utils.getAsString(row,"LevelName")));
        root.setQueryLevel(Utils.getAsString(row,"LevelName"));
        //root.setQueryLevel(Utils.getAsString(row,"QueryLevel"));
        root.setLevel(Utils.getAsNumber(row,"Level"));
        root.setDictShortName(Utils.getAsString(row,"DictShortName"));
        root.setDictContentId(Utils.getAsString(row,"DictContentId"));
        root.setDictContentCode(Utils.getAsString(row,"DictContentCode"));

        root.setApprovedFlag(Utils.getAsString(row,"ApprovedFlag"));
        root.setDictContentAltCode(Utils.getAsString(row,"DictContentAltCode"));
        root.setStatus(Utils.getAsString(row,"Status"));
        root.setPredictGroupId(Utils.getAsNumber(row,"PredictGroupId"));
        root.setHasScope(hasScope);

        root.setTermCategory(Utils.getAsString(row,"Termcat"));
        root.setTermLevel(Utils.getAsString(row,"Termlvl"));
        root.setTermScope(Utils.getAsNumber(row,"Termscp"));
        root.setTermWeight(Utils.getAsString(row,"Termweig"));
        root.setPath(Utils.getAsString(row,"TermPath"));
        root.setFormattedScope(Utils.getAsString(row,"FormattedScope"));
        
        //AMC queryLevel
        Pattern pattern = Pattern.compile("[0-9]");
        Matcher matcher = pattern.matcher(root.getLevelName());
        if (matcher.find())
        {
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
            }
        else {
            root.setMqType(CSMQBean.NMQ);
            root.setEditable(true);
            this.editable = true;
            }   
        
        
        
        
        
        
        // set it as expanded so that it won't get called again
        root.setIsExpanded(true);
        
        populateTreeNodes(root);
        //clean up the hashmap
        parentNodesByLevel = null;
        return true;
        }

    private GenericTreeNode populateTreeNodes(GenericTreeNode node) {

        //store node and level
        if (parentNodesByLevel == null) return null;
        parentNodesByLevel.put(node.getDictContentId(), node);

        if (rows.hasMoreElements()) {
            Row row = (Row)rows.nextElement();
            GenericTreeNode termNode = new GenericTreeNode();
            termNode.setTerm(Utils.getAsString(row,"Term"));
            termNode.setPrikey(Utils.getAsString(row,"Prikey"));
            termNode.setParent(Utils.getAsString(row,"Parent"));
            termNode.setLevelName(resolveTermLevel(Utils.getAsString(row,"LevelName")));
            termNode.setQueryLevel(Utils.getAsString(row,"LevelName"));
            termNode.setLevel(Utils.getAsNumber(row,"Level"));
            termNode.setDictShortName(Utils.getAsString(row,"DictShortName"));
            termNode.setDictContentId(Utils.getAsString(row,"DictContentId"));
            termNode.setDictContentCode(Utils.getAsString(row,"DictContentCode"));
            termNode.setApprovedFlag(Utils.getAsString(row,"ApprovedFlag"));
            termNode.setDictContentAltCode(Utils.getAsString(row,"DictContentAltCode"));
            termNode.setStatus(Utils.getAsString(row,"Status"));
            termNode.setPredictGroupId(Utils.getAsNumber(row,"PredictGroupId"));
            termNode.setPath(Utils.getAsString(row,"TermPath"));
            termNode.setPrimaryLink(Utils.getAsBoolean(row,"PrimLinkFlag"));
            
            if (!termNode.isPrimaryLink()) termNode.setStyle("SECONDARY_LINK");            
                       
            // FOR 'LAZY' LOADING
            //String hasChildren = Utils.getAsString(row,"ChildExists");
            //boolean showMoreChildren = hasChildren.equals("Y") ? true : false;
            
            boolean showMoreChildren = Utils.getAsBoolean(row,"ChildExists");
            if (showMoreChildren) {
                //termNode.setIcon(CSMQBean.getProperty("HAS_CHILDREN_ICON"));
                termNode.setShowHasChildrenButton(true);
                }
            

            termNode.setTermCategory(Utils.getAsString(row,"Termcat"));
            termNode.setTermLevel(Utils.getAsString(row,"Termlvl"));
            termNode.setTermScope(Utils.getAsNumber(row,"Termscp"));
            termNode.setTermWeight(Utils.getAsString(row,"Termweig"));
            termNode.setHasScope(this.hasScope);
            termNode.setEditable(this.editable);
            
   
            termNode.setFormattedScope(Utils.getAsString(row,"FormattedScope"));

            GenericTreeNode parentNode = (GenericTreeNode)parentNodesByLevel.get(termNode.getParent());
            parentNode.getChildren().add(termNode);  // add to the parent
            termNode.setParentNode(parentNode);      // set the parent for the child
            if (root.equals(parentNode)) termNode.setIsDirectRelation(true); // it's a direct relation
            if (parentNode.isIsRoot()) termNode.setDeletable(true); //it's a child of the root - it can be deleted
            
            CSMQBean.logger.info(userBean.getCaller() + " ADDING NODE: " + termNode);
            
            setDerivedRelations(termNode);
            
            populateTreeNodes(termNode);
        }
        return node;
    }

   
     private void showChildren() {
                
        clearKeys (targetTree);
        
        GenericTreeNode newRootNode = null;

        RichTreeTable tree = targetTree;
        RowKeySet droppedValue = targetTree.getSelectedRowKeys();

        Object[] keys = droppedValue.toArray();
        
        for (int i = 0; i <keys.length; i++) {
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
        
        if (newRootNode.isIsExpanded()) return; // don't requery if already done
        
        // REQUERY AND GET THE CHILDREN
        
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("SmallTreeVO1Iterator");
        ViewObject vo = dciterb.getViewObject();
        String parentTermScope = "0";
        if (newRootNode != null)
            parentTermScope = newRootNode.getFormattedScope();  
            
        
        
        // TES CHANGED 23-APR-2014
        int mode = nMQWizardBean.getMode();
        if (mode == CSMQBean.MODE_HISTORIC || mode == CSMQBean.MODE_UPDATE_EXISTING || mode == CSMQBean.MODE_COPY_EXISTING || mode == CSMQBean.MODE_INSERT_NEW)
            vo.setNamedWhereClauseParam("dictVersion", nMQWizardBean.getCurrentVersion());
        //~
 
        vo.setNamedWhereClauseParam("dictContentID", newRootNode.getDictContentId());
        vo.setNamedWhereClauseParam("maxLevels", CSMQBean.getProperty("HIERARCHY_SUBSEQUENT_FETCH"));
        vo.setNamedWhereClauseParam("scopeFilter", parentTermScope);
        vo.setNamedWhereClauseParam("ignorePredict", ignorePredict);
        vo.setNamedWhereClauseParam("returnPrimLinkPath", nMQWizardBean.getParamPrimLinkFlag());
        
        CSMQBean.logger.info(userBean.getCaller() + " ** REQUERY **");
        CSMQBean.logger.info(userBean.getCaller() + " Iterator: SmallTreeVO1Iterator");
        CSMQBean.logger.info(userBean.getCaller() + " dictContentID: " +  newRootNode.getDictContentId());
        CSMQBean.logger.info(userBean.getCaller() + " maxLevels: " +  CSMQBean.getProperty("HIERARCHY_SUBSEQUENT_FETCH"));
        CSMQBean.logger.info(userBean.getCaller() + " scopeFilter: " +  parentTermScope);
        CSMQBean.logger.info(userBean.getCaller() + " ignorePredict:" + ignorePredict);
        CSMQBean.logger.info(userBean.getCaller() + " returnPrimLinkPath:" + nMQWizardBean.getParamPrimLinkFlag());     
        
        vo.executeQuery();
        
        rows = dciterb.getRowSetIterator().enumerateRowsInRange();
        
        // skip the first row, since it is the parent
        rows.nextElement();
                
        parentNodesByLevel = new HashMap();
        populateTreeNodes(newRootNode);
        
        //newRootNode.setIcon(null); // get rid of the icon
        newRootNode.setIsExpanded(true); // prevent it from being called again
        newRootNode.setShowHasChildrenButton(false);
        
        RowKeySet rks = new RowKeySetTreeImpl(true);
        rks.setCollectionModel(treemodel);
        tree.setDisclosedRowKeys(rks);

        AdfFacesContext.getCurrentInstance().addPartialTarget(tree); 
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(tree);
    }
         
 
   
    private DCIteratorBinding resolveTargetIterWithSpel(String spelExpr){
        FacesContext fctx = FacesContext.getCurrentInstance();
        ELContext elctx = fctx.getELContext();
        ExpressionFactory elFactory = fctx.getApplication().getExpressionFactory();
        ValueExpression valueExpr = elFactory.createValueExpression(elctx, spelExpr,Object.class);
        DCIteratorBinding dciter = (DCIteratorBinding) valueExpr.getValue(elctx);   
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
        showChildren();
        }

    
    public void refresh () {
        this.refresh(null, root.getDictContentId());
    }
    
    public void refresh(ActionEvent actionEvent) {
        refresh (actionEvent, root.getDictContentId());
        showStatus(CSMQBean.MQ_REFRESHED);
        }
    
    public void refresh(ActionEvent actionEvent, String rootNode) {
        
        clearKeys (targetTree);
        clearKeys (sourceTree);
               
        // REQUERY 
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("SmallTreeVO1Iterator");
        ViewObject vo = dciterb.getViewObject();

        //vo.setNamedWhereClauseParam("activationGroup", nMQWizardBean.getCurrentPredictGroups());
        vo.setNamedWhereClauseParam("dictContentID", rootNode);
        vo.setNamedWhereClauseParam("maxLevels", CSMQBean.getProperty("HIERARCHY_SUBSEQUENT_FETCH"));
        vo.setNamedWhereClauseParam("ignorePredict", ignorePredict);
        
        vo.executeQuery();
        init(this.hasScope);
        
        //expand the tree
        RowKeySet disclosedRowKeys = new RowKeySetTreeImpl(true); 
        disclosedRowKeys.setCollectionModel(ModelUtils.toTreeModel(targetTree.getValue()));
        targetTree.setDisclosedRowKeys(disclosedRowKeys);

            
        AdfFacesContext.getCurrentInstance().addPartialTarget(targetTree); 
        AdfFacesContext.getCurrentInstance().partialUpdateNotify(targetTree);
        }
    
    public void clear () {
        refresh (null, "0");
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
    
    public String getQueryLevel(){
        int p = queryLevel.indexOf("Q");
        return queryLevel.substring(p);
    }
    
    
    private String resolveTermLevel (String level) {

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
