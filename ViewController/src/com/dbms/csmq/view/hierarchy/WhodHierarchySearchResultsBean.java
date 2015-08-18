package com.dbms.csmq.view.hierarchy;


import com.dbms.csmq.CSMQBean;
import com.dbms.csmq.UserBean;
import com.dbms.util.Utils;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import oracle.adf.model.BindingContext;
import oracle.adf.model.binding.DCBindingContainer;
import oracle.adf.model.binding.DCIteratorBinding;
import oracle.adf.share.ADFContext;

import oracle.jbo.Row;

import org.apache.myfaces.trinidad.model.ChildPropertyTreeModel;
import org.apache.myfaces.trinidad.model.TreeModel;


public class WhodHierarchySearchResultsBean {
    private TreeModel treemodel;
    private Enumeration rows;
    private List nodes;
    UserBean userBean = (UserBean)ADFContext.getCurrent().getSessionScope().get("UserBean");

    public WhodHierarchySearchResultsBean() {
    }

    public void init() {
        nodes = new ArrayList();
        createTree();
        treemodel = new ChildPropertyTreeModel(nodes, "children") {
                public boolean isContainer() {
                    return false;
                }
            };
    }

    private void createTree() {
        CSMQBean.logger.info(userBean.getCaller() + " <<< CREATING RESULTS LIST LIST  >>>");
        BindingContext bc = BindingContext.getCurrent();
        DCBindingContainer binding = (DCBindingContainer)bc.getCurrentBindingsEntry();
        DCIteratorBinding dciterb = (DCIteratorBinding)binding.get("WhodHierarchySourceTermSearchVO1Iterator");
        rows = dciterb.getRowSetIterator().enumerateRowsInRange();
        while (rows.hasMoreElements()) {
            Row row = (Row)rows.nextElement();
            GenericTreeNode termNode = new GenericTreeNode();
            termNode.setTerm(Utils.getAsString(row, "Term"));
            CSMQBean.logger.debug(userBean.getCaller() + " ADDING CHILD: " + termNode.getTerm());
            //termNode.setLevelName(resolveTermLevel(Utils.getAsString(row, "LevelName")));
            termNode.setQueryLevel(Utils.getAsString(row, "Querylevel"));
            termNode.setLevelName(Utils.getAsString(row, "LevelName"));
            termNode.setDictContentId(Utils.getAsString(row, "DictContentId"));
            termNode.setDictContentCode(Utils.getAsString(row, "DictContentCode"));
            // generate a temp prikey to use as a key for inserts in the hierarchy assessor
            termNode.setPrikey(termNode.getDictContentId() + "." + termNode.getDictContentCode());
            //  TODO: UNCOMMENT CSMQBean.logger.info(userBean.getCaller() + " [PT LIST] ADDING: " + termNode);
            nodes.add(termNode);
        }
    }

    public void setTreemodel(TreeModel treemodel) {
        this.treemodel = treemodel;
    }

    public TreeModel getTreemodel() {
        return treemodel;
    }
}
