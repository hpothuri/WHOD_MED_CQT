package com.dbms.csmq.model.WHOD;

import oracle.jbo.server.ViewObjectImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Wed Jun 10 14:48:38 IST 2015
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class WHODSmallTreeVOImpl extends ViewObjectImpl {
    /**
     * This is the default constructor (do not remove).
     */
    public WHODSmallTreeVOImpl() {
    }

    /**
     * Returns the bind variable value for dictContentID.
     * @return bind variable value for dictContentID
     */
    public String getdictContentID() {
        return (String)getNamedWhereClauseParam("dictContentID");
    }

    /**
     * Sets <code>value</code> for bind variable dictContentID.
     * @param value value to bind as dictContentID
     */
    public void setdictContentID(String value) {
        setNamedWhereClauseParam("dictContentID", value);
    }
}
