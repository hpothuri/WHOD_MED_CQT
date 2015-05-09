package com.dbms.csmq.model.search;

import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.ViewRowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Mon May 09 15:19:07 EDT 2011
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class SimpleSearchRowImpl extends ViewRowImpl {
    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        Prikey {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getPrikey();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setPrikey((String)value);
            }
        }
        ,
        MqgroupF {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getMqgroupF();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setMqgroupF((String)value);
            }
        }
        ,
        DictNm {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getDictNm();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setDictNm((String)value);
            }
        }
        ,
        LevelNm {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getLevelNm();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setLevelNm((String)value);
            }
        }
        ,
        DomainNm {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getDomainNm();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setDomainNm((String)value);
            }
        }
        ,
        ApprFlag {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getApprFlag();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setApprFlag((String)value);
            }
        }
        ,
        TermSubtype {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getTermSubtype();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setTermSubtype((String)value);
            }
        }
        ,
        ContentId {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getContentId();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setContentId((Number)value);
            }
        }
        ,
        Version {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getVersion();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setVersion((String)value);
            }
        }
        ,
        Mqtransid {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getMqtransid();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setMqtransid((Number)value);
            }
        }
        ,
        Mqlevel {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getMqlevel();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setMqlevel((String)value);
            }
        }
        ,
        Mqcode {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getMqcode();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setMqcode((String)value);
            }
        }
        ,
        Mqterm {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getMqterm();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setMqterm((String)value);
            }
        }
        ,
        Mqstatus {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getMqstatus();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setMqstatus((String)value);
            }
        }
        ,
        Mqscp {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getMqscp();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setMqscp((String)value);
            }
        }
        ,
        Mqalgo {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getMqalgo();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setMqalgo((String)value);
            }
        }
        ,
        Mqgroup {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getMqgroup();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setMqgroup((String)value);
            }
        }
        ,
        Mqprodct {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getMqprodct();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setMqprodct((String)value);
            }
        }
        ,
        Mqcrtev {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getMqcrtev();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setMqcrtev((String)value);
            }
        }
        ,
        Mqaltcode {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getMqaltcode();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setMqaltcode((String)value);
            }
        }
        ,
        CurPendStatus {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getCurPendStatus();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setCurPendStatus((String)value);
            }
        }
        ,
        Dates {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getDates();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setDates((Date)value);
            }
        }
        ,
        Createdby {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getCreatedby();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setCreatedby((String)value);
            }
        }
        ,
        Groupname {
            public Object get(SimpleSearchRowImpl obj) {
                return obj.getGroupname();
            }

            public void put(SimpleSearchRowImpl obj, Object value) {
                obj.setGroupname((String)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(SimpleSearchRowImpl object);

        public abstract void put(SimpleSearchRowImpl object, Object value);

        public int index() {
            return AttributesEnum.firstIndex() + ordinal();
        }

        public static int firstIndex() {
            return firstIndex;
        }

        public static int count() {
            return AttributesEnum.firstIndex() + AttributesEnum.staticValues().length;
        }

        public static AttributesEnum[] staticValues() {
            if (vals == null) {
                vals = AttributesEnum.values();
            }
            return vals;
        }
    }


    public static final int PRIKEY = AttributesEnum.Prikey.index();
    public static final int MQGROUPF = AttributesEnum.MqgroupF.index();
    public static final int DICTNM = AttributesEnum.DictNm.index();
    public static final int LEVELNM = AttributesEnum.LevelNm.index();
    public static final int DOMAINNM = AttributesEnum.DomainNm.index();
    public static final int APPRFLAG = AttributesEnum.ApprFlag.index();
    public static final int TERMSUBTYPE = AttributesEnum.TermSubtype.index();
    public static final int CONTENTID = AttributesEnum.ContentId.index();
    public static final int VERSION = AttributesEnum.Version.index();
    public static final int MQTRANSID = AttributesEnum.Mqtransid.index();
    public static final int MQLEVEL = AttributesEnum.Mqlevel.index();
    public static final int MQCODE = AttributesEnum.Mqcode.index();
    public static final int MQTERM = AttributesEnum.Mqterm.index();
    public static final int MQSTATUS = AttributesEnum.Mqstatus.index();
    public static final int MQSCP = AttributesEnum.Mqscp.index();
    public static final int MQALGO = AttributesEnum.Mqalgo.index();
    public static final int MQGROUP = AttributesEnum.Mqgroup.index();
    public static final int MQPRODCT = AttributesEnum.Mqprodct.index();
    public static final int MQCRTEV = AttributesEnum.Mqcrtev.index();
    public static final int MQALTCODE = AttributesEnum.Mqaltcode.index();
    public static final int CURPENDSTATUS = AttributesEnum.CurPendStatus.index();
    public static final int DATES = AttributesEnum.Dates.index();
    public static final int CREATEDBY = AttributesEnum.Createdby.index();
    public static final int GROUPNAME = AttributesEnum.Groupname.index();

    /**
     * This is the default constructor (do not remove).
     */
    public SimpleSearchRowImpl() {
    }

    /**
     * Gets the attribute value for the calculated attribute Prikey.
     * @return the Prikey
     */
    public String getPrikey() {
        return (String) getAttributeInternal(PRIKEY);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Prikey.
     * @param value value to set the  Prikey
     */
    public void setPrikey(String value) {
        setAttributeInternal(PRIKEY, value);
    }

    /**
     * Gets the attribute value for the calculated attribute MqgroupF.
     * @return the MqgroupF
     */
    public String getMqgroupF() {
        return (String) getAttributeInternal(MQGROUPF);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute MqgroupF.
     * @param value value to set the  MqgroupF
     */
    public void setMqgroupF(String value) {
        setAttributeInternal(MQGROUPF, value);
    }

    /**
     * Gets the attribute value for the calculated attribute product.
     * @return the product
     */
    public String getDictNm() {
        return (String) getAttributeInternal(DICTNM);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute product.
     * @param value value to set the  product
     */
    public void setDictNm(String value) {
        setAttributeInternal(DICTNM, value);
    }

    /**
     * Gets the attribute value for the calculated attribute LevelNm.
     * @return the LevelNm
     */
    public String getLevelNm() {
        return (String) getAttributeInternal(LEVELNM);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute LevelNm.
     * @param value value to set the  LevelNm
     */
    public void setLevelNm(String value) {
        setAttributeInternal(LEVELNM, value);
    }

    /**
     * Gets the attribute value for the calculated attribute DomainNm.
     * @return the DomainNm
     */
    public String getDomainNm() {
        return (String) getAttributeInternal(DOMAINNM);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute DomainNm.
     * @param value value to set the  DomainNm
     */
    public void setDomainNm(String value) {
        setAttributeInternal(DOMAINNM, value);
    }

    /**
     * Gets the attribute value for the calculated attribute ApprFlag.
     * @return the ApprFlag
     */
    public String getApprFlag() {
        return (String) getAttributeInternal(APPRFLAG);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute ApprFlag.
     * @param value value to set the  ApprFlag
     */
    public void setApprFlag(String value) {
        setAttributeInternal(APPRFLAG, value);
    }

    /**
     * Gets the attribute value for the calculated attribute TermSubtype.
     * @return the TermSubtype
     */
    public String getTermSubtype() {
        return (String) getAttributeInternal(TERMSUBTYPE);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute TermSubtype.
     * @param value value to set the  TermSubtype
     */
    public void setTermSubtype(String value) {
        setAttributeInternal(TERMSUBTYPE, value);
    }

    /**
     * Gets the attribute value for the calculated attribute ContentId.
     * @return the ContentId
     */
    public Number getContentId() {
        return (Number) getAttributeInternal(CONTENTID);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute ContentId.
     * @param value value to set the  ContentId
     */
    public void setContentId(Number value) {
        setAttributeInternal(CONTENTID, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Version.
     * @return the Version
     */
    public String getVersion() {
        return (String) getAttributeInternal(VERSION);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Version.
     * @param value value to set the  Version
     */
    public void setVersion(String value) {
        setAttributeInternal(VERSION, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Mqtransid.
     * @return the Mqtransid
     */
    public Number getMqtransid() {
        return (Number) getAttributeInternal(MQTRANSID);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Mqtransid.
     * @param value value to set the  Mqtransid
     */
    public void setMqtransid(Number value) {
        setAttributeInternal(MQTRANSID, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Mqlevel.
     * @return the Mqlevel
     */
    public String getMqlevel() {
        return (String) getAttributeInternal(MQLEVEL);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Mqlevel.
     * @param value value to set the  Mqlevel
     */
    public void setMqlevel(String value) {
        setAttributeInternal(MQLEVEL, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Mqcode.
     * @return the Mqcode
     */
    public String getMqcode() {
        return (String) getAttributeInternal(MQCODE);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Mqcode.
     * @param value value to set the  Mqcode
     */
    public void setMqcode(String value) {
        setAttributeInternal(MQCODE, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Mqterm.
     * @return the Mqterm
     */
    public String getMqterm() {
        return (String) getAttributeInternal(MQTERM);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Mqterm.
     * @param value value to set the  Mqterm
     */
    public void setMqterm(String value) {
        setAttributeInternal(MQTERM, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Mqstatus.
     * @return the Mqstatus
     */
    public String getMqstatus() {
        return (String) getAttributeInternal(MQSTATUS);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Mqstatus.
     * @param value value to set the  Mqstatus
     */
    public void setMqstatus(String value) {
        setAttributeInternal(MQSTATUS, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Mqscp.
     * @return the Mqscp
     */
    public String getMqscp() {
        return (String) getAttributeInternal(MQSCP);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Mqscp.
     * @param value value to set the  Mqscp
     */
    public void setMqscp(String value) {
        setAttributeInternal(MQSCP, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Mqalgo.
     * @return the Mqalgo
     */
    public String getMqalgo() {
        return (String) getAttributeInternal(MQALGO);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Mqalgo.
     * @param value value to set the  Mqalgo
     */
    public void setMqalgo(String value) {
        setAttributeInternal(MQALGO, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Mqgroup.
     * @return the Mqgroup
     */
    public String getMqgroup() {
        return (String) getAttributeInternal(MQGROUP);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Mqgroup.
     * @param value value to set the  Mqgroup
     */
    public void setMqgroup(String value) {
        setAttributeInternal(MQGROUP, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Mqprodct.
     * @return the Mqprodct
     */
    public String getMqprodct() {
        return (String) getAttributeInternal(MQPRODCT);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Mqprodct.
     * @param value value to set the  Mqprodct
     */
    public void setMqprodct(String value) {
        setAttributeInternal(MQPRODCT, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Mqcrtev.
     * @return the Mqcrtev
     */
    public String getMqcrtev() {
        return (String) getAttributeInternal(MQCRTEV);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Mqcrtev.
     * @param value value to set the  Mqcrtev
     */
    public void setMqcrtev(String value) {
        setAttributeInternal(MQCRTEV, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Mqaltcode.
     * @return the Mqaltcode
     */
    public String getMqaltcode() {
        return (String) getAttributeInternal(MQALTCODE);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Mqaltcode.
     * @param value value to set the  Mqaltcode
     */
    public void setMqaltcode(String value) {
        setAttributeInternal(MQALTCODE, value);
    }

    /**
     * Gets the attribute value for the calculated attribute CurPendStatus.
     * @return the CurPendStatus
     */
    public String getCurPendStatus() {
        return (String) getAttributeInternal(CURPENDSTATUS);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute CurPendStatus.
     * @param value value to set the  CurPendStatus
     */
    public void setCurPendStatus(String value) {
        setAttributeInternal(CURPENDSTATUS, value);
    }


    /**
     * Gets the attribute value for the calculated attribute Dates.
     * @return the Dates
     */
    public Date getDates() {
        return (Date) getAttributeInternal(DATES);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Dates.
     * @param value value to set the  Dates
     */
    public void setDates(Date value) {
        setAttributeInternal(DATES, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Createdby.
     * @return the Createdby
     */
    public String getCreatedby() {
        return (String) getAttributeInternal(CREATEDBY);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Createdby.
     * @param value value to set the  Createdby
     */
    public void setCreatedby(String value) {
        setAttributeInternal(CREATEDBY, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Groupname.
     * @return the Groupname
     */
    public String getGroupname() {
        return (String) getAttributeInternal(GROUPNAME);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Groupname.
     * @param value value to set the  Groupname
     */
    public void setGroupname(String value) {
        setAttributeInternal(GROUPNAME, value);
    }

    /**
     * getAttrInvokeAccessor: generated method. Do not modify.
     * @param index the index identifying the attribute
     * @param attrDef the attribute

     * @return the attribute value
     * @throws Exception
     */
    protected Object getAttrInvokeAccessor(int index, AttributeDefImpl attrDef) throws Exception {
        if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
            return AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].get(this);
        }
        return super.getAttrInvokeAccessor(index, attrDef);
    }

    /**
     * setAttrInvokeAccessor: generated method. Do not modify.
     * @param index the index identifying the attribute
     * @param value the value to assign to the attribute
     * @param attrDef the attribute

     * @throws Exception
     */
    protected void setAttrInvokeAccessor(int index, Object value, AttributeDefImpl attrDef) throws Exception {
        if ((index >= AttributesEnum.firstIndex()) && (index < AttributesEnum.count())) {
            AttributesEnum.staticValues()[index - AttributesEnum.firstIndex()].put(this, value);
            return;
        }
        super.setAttrInvokeAccessor(index, value, attrDef);
    }
}