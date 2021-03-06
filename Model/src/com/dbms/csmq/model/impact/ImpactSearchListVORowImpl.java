package com.dbms.csmq.model.impact;

import oracle.jbo.domain.Date;
import oracle.jbo.domain.Number;
import oracle.jbo.server.AttributeDefImpl;
import oracle.jbo.server.ViewRowImpl;
// ---------------------------------------------------------------------
// ---    File generated by Oracle ADF Business Components Design Time.
// ---    Tue Feb 07 16:04:41 EST 2012
// ---    Custom code may be added to this class.
// ---    Warning: Do not modify method signatures of generated methods.
// ---------------------------------------------------------------------
public class ImpactSearchListVORowImpl extends ViewRowImpl {
    /**
     * AttributesEnum: generated enum for identifying attributes and accessors. Do not modify.
     */
    public enum AttributesEnum {
        ImpactYn {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getImpactYn();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setImpactYn((String)value);
            }
        }
        ,
        NmqSmq {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getNmqSmq();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setNmqSmq((String)value);
            }
        }
        ,
        DictContentId {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getDictContentId();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setDictContentId((Number)value);
            }
        }
        ,
        PredictGroupId {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getPredictGroupId();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setPredictGroupId((Number)value);
            }
        }
        ,
        DictContentEntryTs {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getDictContentEntryTs();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setDictContentEntryTs((Date)value);
            }
        }
        ,
        CreatedBy {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getCreatedBy();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setCreatedBy((String)value);
            }
        }
        ,
        DefDictionaryId {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getDefDictionaryId();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setDefDictionaryId((Number)value);
            }
        }
        ,
        DictShortName {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getDictShortName();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setDictShortName((String)value);
            }
        }
        ,
        DefLevelId {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getDefLevelId();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setDefLevelId((Number)value);
            }
        }
        ,
        DefLevelsShortName {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getDefLevelsShortName();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setDefLevelsShortName((String)value);
            }
        }
        ,
        DefDomainId {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getDefDomainId();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setDefDomainId((Number)value);
            }
        }
        ,
        TermScopeId {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getTermScopeId();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setTermScopeId((Number)value);
            }
        }
        ,
        ApprovedFlag {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getApprovedFlag();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setApprovedFlag((String)value);
            }
        }
        ,
        EndTs {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getEndTs();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setEndTs((Date)value);
            }
        }
        ,
        TermType {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getTermType();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setTermType((String)value);
            }
        }
        ,
        TermSubtype {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getTermSubtype();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setTermSubtype((String)value);
            }
        }
        ,
        ActivationId {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getActivationId();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setActivationId((Number)value);
            }
        }
        ,
        TermUpper {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getTermUpper();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setTermUpper((String)value);
            }
        }
        ,
        Term {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getTerm();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setTerm((String)value);
            }
        }
        ,
        TermLanguage {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getTermLanguage();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setTermLanguage((String)value);
            }
        }
        ,
        DefSearchId {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getDefSearchId();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setDefSearchId((Number)value);
            }
        }
        ,
        TransactionId {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getTransactionId();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setTransactionId((Number)value);
            }
        }
        ,
        ModifiedBy {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getModifiedBy();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setModifiedBy((String)value);
            }
        }
        ,
        Status {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getStatus();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setStatus((String)value);
            }
        }
        ,
        Category {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getCategory();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setCategory((String)value);
            }
        }
        ,
        DictContentCode {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getDictContentCode();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setDictContentCode((String)value);
            }
        }
        ,
        DictContentAltCode {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getDictContentAltCode();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setDictContentAltCode((String)value);
            }
        }
        ,
        Value1 {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getValue1();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setValue1((String)value);
            }
        }
        ,
        Value2 {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getValue2();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setValue2((String)value);
            }
        }
        ,
        Value3 {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getValue3();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setValue3((String)value);
            }
        }
        ,
        Value4 {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getValue4();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setValue4((String)value);
            }
        }
        ,
        CommentText {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getCommentText();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setCommentText((String)value);
            }
        }
        ,
        ImpactStatus {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getImpactStatus();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setImpactStatus((String)value);
            }
        }
        ,
        ReleaseGroup {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getReleaseGroup();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setReleaseGroup((String)value);
            }
        }
        ,
        LastRefreshDate {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getLastRefreshDate();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setLastRefreshDate((Date)value);
            }
        }
        ,
        WorkflowState {
            public Object get(ImpactSearchListVORowImpl obj) {
                return obj.getWorkflowState();
            }

            public void put(ImpactSearchListVORowImpl obj, Object value) {
                obj.setWorkflowState((String)value);
            }
        }
        ;
        private static AttributesEnum[] vals = null;
        private static int firstIndex = 0;

        public abstract Object get(ImpactSearchListVORowImpl object);

        public abstract void put(ImpactSearchListVORowImpl object, Object value);

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


    public static final int IMPACTYN = AttributesEnum.ImpactYn.index();
    public static final int NMQSMQ = AttributesEnum.NmqSmq.index();
    public static final int DICTCONTENTID = AttributesEnum.DictContentId.index();
    public static final int PREDICTGROUPID = AttributesEnum.PredictGroupId.index();
    public static final int DICTCONTENTENTRYTS = AttributesEnum.DictContentEntryTs.index();
    public static final int CREATEDBY = AttributesEnum.CreatedBy.index();
    public static final int DEFDICTIONARYID = AttributesEnum.DefDictionaryId.index();
    public static final int DICTSHORTNAME = AttributesEnum.DictShortName.index();
    public static final int DEFLEVELID = AttributesEnum.DefLevelId.index();
    public static final int DEFLEVELSSHORTNAME = AttributesEnum.DefLevelsShortName.index();
    public static final int DEFDOMAINID = AttributesEnum.DefDomainId.index();
    public static final int TERMSCOPEID = AttributesEnum.TermScopeId.index();
    public static final int APPROVEDFLAG = AttributesEnum.ApprovedFlag.index();
    public static final int ENDTS = AttributesEnum.EndTs.index();
    public static final int TERMTYPE = AttributesEnum.TermType.index();
    public static final int TERMSUBTYPE = AttributesEnum.TermSubtype.index();
    public static final int ACTIVATIONID = AttributesEnum.ActivationId.index();
    public static final int TERMUPPER = AttributesEnum.TermUpper.index();
    public static final int TERM = AttributesEnum.Term.index();
    public static final int TERMLANGUAGE = AttributesEnum.TermLanguage.index();
    public static final int DEFSEARCHID = AttributesEnum.DefSearchId.index();
    public static final int TRANSACTIONID = AttributesEnum.TransactionId.index();
    public static final int MODIFIEDBY = AttributesEnum.ModifiedBy.index();
    public static final int STATUS = AttributesEnum.Status.index();
    public static final int CATEGORY = AttributesEnum.Category.index();
    public static final int DICTCONTENTCODE = AttributesEnum.DictContentCode.index();
    public static final int DICTCONTENTALTCODE = AttributesEnum.DictContentAltCode.index();
    public static final int VALUE1 = AttributesEnum.Value1.index();
    public static final int VALUE2 = AttributesEnum.Value2.index();
    public static final int VALUE3 = AttributesEnum.Value3.index();
    public static final int VALUE4 = AttributesEnum.Value4.index();
    public static final int COMMENTTEXT = AttributesEnum.CommentText.index();
    public static final int IMPACTSTATUS = AttributesEnum.ImpactStatus.index();
    public static final int RELEASEGROUP = AttributesEnum.ReleaseGroup.index();
    public static final int LASTREFRESHDATE = AttributesEnum.LastRefreshDate.index();
    public static final int WORKFLOWSTATE = AttributesEnum.WorkflowState.index();

    /**
     * This is the default constructor (do not remove).
     */
    public ImpactSearchListVORowImpl() {
    }

    /**
     * Gets the attribute value for the calculated attribute ImpactYn.
     * @return the ImpactYn
     */
    public String getImpactYn() {
        return (String) getAttributeInternal(IMPACTYN);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute ImpactYn.
     * @param value value to set the  ImpactYn
     */
    public void setImpactYn(String value) {
        setAttributeInternal(IMPACTYN, value);
    }

    /**
     * Gets the attribute value for the calculated attribute NmqSmq.
     * @return the NmqSmq
     */
    public String getNmqSmq() {
        return (String) getAttributeInternal(NMQSMQ);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute NmqSmq.
     * @param value value to set the  NmqSmq
     */
    public void setNmqSmq(String value) {
        setAttributeInternal(NMQSMQ, value);
    }

    /**
     * Gets the attribute value for the calculated attribute DictContentId.
     * @return the DictContentId
     */
    public Number getDictContentId() {
        return (Number) getAttributeInternal(DICTCONTENTID);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute DictContentId.
     * @param value value to set the  DictContentId
     */
    public void setDictContentId(Number value) {
        setAttributeInternal(DICTCONTENTID, value);
    }

    /**
     * Gets the attribute value for the calculated attribute PredictGroupId.
     * @return the PredictGroupId
     */
    public Number getPredictGroupId() {
        return (Number) getAttributeInternal(PREDICTGROUPID);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute PredictGroupId.
     * @param value value to set the  PredictGroupId
     */
    public void setPredictGroupId(Number value) {
        setAttributeInternal(PREDICTGROUPID, value);
    }

    /**
     * Gets the attribute value for the calculated attribute DictContentEntryTs.
     * @return the DictContentEntryTs
     */
    public Date getDictContentEntryTs() {
        return (Date) getAttributeInternal(DICTCONTENTENTRYTS);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute DictContentEntryTs.
     * @param value value to set the  DictContentEntryTs
     */
    public void setDictContentEntryTs(Date value) {
        setAttributeInternal(DICTCONTENTENTRYTS, value);
    }

    /**
     * Gets the attribute value for the calculated attribute CreatedBy.
     * @return the CreatedBy
     */
    public String getCreatedBy() {
        return (String) getAttributeInternal(CREATEDBY);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute CreatedBy.
     * @param value value to set the  CreatedBy
     */
    public void setCreatedBy(String value) {
        setAttributeInternal(CREATEDBY, value);
    }

    /**
     * Gets the attribute value for the calculated attribute DefDictionaryId.
     * @return the DefDictionaryId
     */
    public Number getDefDictionaryId() {
        return (Number) getAttributeInternal(DEFDICTIONARYID);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute DefDictionaryId.
     * @param value value to set the  DefDictionaryId
     */
    public void setDefDictionaryId(Number value) {
        setAttributeInternal(DEFDICTIONARYID, value);
    }

    /**
     * Gets the attribute value for the calculated attribute DictShortName.
     * @return the DictShortName
     */
    public String getDictShortName() {
        return (String) getAttributeInternal(DICTSHORTNAME);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute DictShortName.
     * @param value value to set the  DictShortName
     */
    public void setDictShortName(String value) {
        setAttributeInternal(DICTSHORTNAME, value);
    }

    /**
     * Gets the attribute value for the calculated attribute DefLevelId.
     * @return the DefLevelId
     */
    public Number getDefLevelId() {
        return (Number) getAttributeInternal(DEFLEVELID);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute DefLevelId.
     * @param value value to set the  DefLevelId
     */
    public void setDefLevelId(Number value) {
        setAttributeInternal(DEFLEVELID, value);
    }

    /**
     * Gets the attribute value for the calculated attribute DefLevelsShortName.
     * @return the DefLevelsShortName
     */
    public String getDefLevelsShortName() {
        return (String) getAttributeInternal(DEFLEVELSSHORTNAME);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute DefLevelsShortName.
     * @param value value to set the  DefLevelsShortName
     */
    public void setDefLevelsShortName(String value) {
        setAttributeInternal(DEFLEVELSSHORTNAME, value);
    }

    /**
     * Gets the attribute value for the calculated attribute DefDomainId.
     * @return the DefDomainId
     */
    public Number getDefDomainId() {
        return (Number) getAttributeInternal(DEFDOMAINID);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute DefDomainId.
     * @param value value to set the  DefDomainId
     */
    public void setDefDomainId(Number value) {
        setAttributeInternal(DEFDOMAINID, value);
    }

    /**
     * Gets the attribute value for the calculated attribute TermScopeId.
     * @return the TermScopeId
     */
    public Number getTermScopeId() {
        return (Number) getAttributeInternal(TERMSCOPEID);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute TermScopeId.
     * @param value value to set the  TermScopeId
     */
    public void setTermScopeId(Number value) {
        setAttributeInternal(TERMSCOPEID, value);
    }

    /**
     * Gets the attribute value for the calculated attribute ApprovedFlag.
     * @return the ApprovedFlag
     */
    public String getApprovedFlag() {
        return (String) getAttributeInternal(APPROVEDFLAG);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute ApprovedFlag.
     * @param value value to set the  ApprovedFlag
     */
    public void setApprovedFlag(String value) {
        setAttributeInternal(APPROVEDFLAG, value);
    }

    /**
     * Gets the attribute value for the calculated attribute EndTs.
     * @return the EndTs
     */
    public Date getEndTs() {
        return (Date) getAttributeInternal(ENDTS);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute EndTs.
     * @param value value to set the  EndTs
     */
    public void setEndTs(Date value) {
        setAttributeInternal(ENDTS, value);
    }

    /**
     * Gets the attribute value for the calculated attribute TermType.
     * @return the TermType
     */
    public String getTermType() {
        return (String) getAttributeInternal(TERMTYPE);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute TermType.
     * @param value value to set the  TermType
     */
    public void setTermType(String value) {
        setAttributeInternal(TERMTYPE, value);
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
     * Gets the attribute value for the calculated attribute ActivationId.
     * @return the ActivationId
     */
    public Number getActivationId() {
        return (Number) getAttributeInternal(ACTIVATIONID);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute ActivationId.
     * @param value value to set the  ActivationId
     */
    public void setActivationId(Number value) {
        setAttributeInternal(ACTIVATIONID, value);
    }

    /**
     * Gets the attribute value for the calculated attribute TermUpper.
     * @return the TermUpper
     */
    public String getTermUpper() {
        return (String) getAttributeInternal(TERMUPPER);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute TermUpper.
     * @param value value to set the  TermUpper
     */
    public void setTermUpper(String value) {
        setAttributeInternal(TERMUPPER, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Term.
     * @return the Term
     */
    public String getTerm() {
        return (String) getAttributeInternal(TERM);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Term.
     * @param value value to set the  Term
     */
    public void setTerm(String value) {
        setAttributeInternal(TERM, value);
    }

    /**
     * Gets the attribute value for the calculated attribute TermLanguage.
     * @return the TermLanguage
     */
    public String getTermLanguage() {
        return (String) getAttributeInternal(TERMLANGUAGE);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute TermLanguage.
     * @param value value to set the  TermLanguage
     */
    public void setTermLanguage(String value) {
        setAttributeInternal(TERMLANGUAGE, value);
    }

    /**
     * Gets the attribute value for the calculated attribute DefSearchId.
     * @return the DefSearchId
     */
    public Number getDefSearchId() {
        return (Number) getAttributeInternal(DEFSEARCHID);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute DefSearchId.
     * @param value value to set the  DefSearchId
     */
    public void setDefSearchId(Number value) {
        setAttributeInternal(DEFSEARCHID, value);
    }

    /**
     * Gets the attribute value for the calculated attribute TransactionId.
     * @return the TransactionId
     */
    public Number getTransactionId() {
        return (Number) getAttributeInternal(TRANSACTIONID);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute TransactionId.
     * @param value value to set the  TransactionId
     */
    public void setTransactionId(Number value) {
        setAttributeInternal(TRANSACTIONID, value);
    }

    /**
     * Gets the attribute value for the calculated attribute ModifiedBy.
     * @return the ModifiedBy
     */
    public String getModifiedBy() {
        return (String) getAttributeInternal(MODIFIEDBY);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute ModifiedBy.
     * @param value value to set the  ModifiedBy
     */
    public void setModifiedBy(String value) {
        setAttributeInternal(MODIFIEDBY, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Status.
     * @return the Status
     */
    public String getStatus() {
        return (String) getAttributeInternal(STATUS);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Status.
     * @param value value to set the  Status
     */
    public void setStatus(String value) {
        setAttributeInternal(STATUS, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Category.
     * @return the Category
     */
    public String getCategory() {
        return (String) getAttributeInternal(CATEGORY);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Category.
     * @param value value to set the  Category
     */
    public void setCategory(String value) {
        setAttributeInternal(CATEGORY, value);
    }

    /**
     * Gets the attribute value for the calculated attribute DictContentCode.
     * @return the DictContentCode
     */
    public String getDictContentCode() {
        return (String) getAttributeInternal(DICTCONTENTCODE);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute DictContentCode.
     * @param value value to set the  DictContentCode
     */
    public void setDictContentCode(String value) {
        setAttributeInternal(DICTCONTENTCODE, value);
    }

    /**
     * Gets the attribute value for the calculated attribute DictContentAltCode.
     * @return the DictContentAltCode
     */
    public String getDictContentAltCode() {
        return (String) getAttributeInternal(DICTCONTENTALTCODE);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute DictContentAltCode.
     * @param value value to set the  DictContentAltCode
     */
    public void setDictContentAltCode(String value) {
        setAttributeInternal(DICTCONTENTALTCODE, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Value1.
     * @return the Value1
     */
    public String getValue1() {
        return (String) getAttributeInternal(VALUE1);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Value1.
     * @param value value to set the  Value1
     */
    public void setValue1(String value) {
        setAttributeInternal(VALUE1, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Value2.
     * @return the Value2
     */
    public String getValue2() {
        return (String) getAttributeInternal(VALUE2);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Value2.
     * @param value value to set the  Value2
     */
    public void setValue2(String value) {
        setAttributeInternal(VALUE2, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Value3.
     * @return the Value3
     */
    public String getValue3() {
        return (String) getAttributeInternal(VALUE3);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Value3.
     * @param value value to set the  Value3
     */
    public void setValue3(String value) {
        setAttributeInternal(VALUE3, value);
    }

    /**
     * Gets the attribute value for the calculated attribute Value4.
     * @return the Value4
     */
    public String getValue4() {
        return (String) getAttributeInternal(VALUE4);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute Value4.
     * @param value value to set the  Value4
     */
    public void setValue4(String value) {
        setAttributeInternal(VALUE4, value);
    }

    /**
     * Gets the attribute value for the calculated attribute CommentText.
     * @return the CommentText
     */
    public String getCommentText() {
        return (String) getAttributeInternal(COMMENTTEXT);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute CommentText.
     * @param value value to set the  CommentText
     */
    public void setCommentText(String value) {
        setAttributeInternal(COMMENTTEXT, value);
    }

    /**
     * Gets the attribute value for the calculated attribute ImpactStatus.
     * @return the ImpactStatus
     */
    public String getImpactStatus() {
        return (String) getAttributeInternal(IMPACTSTATUS);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute ImpactStatus.
     * @param value value to set the  ImpactStatus
     */
    public void setImpactStatus(String value) {
        setAttributeInternal(IMPACTSTATUS, value);
    }

    /**
     * Gets the attribute value for the calculated attribute ReleaseGroup.
     * @return the ReleaseGroup
     */
    public String getReleaseGroup() {
        return (String) getAttributeInternal(RELEASEGROUP);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute ReleaseGroup.
     * @param value value to set the  ReleaseGroup
     */
    public void setReleaseGroup(String value) {
        setAttributeInternal(RELEASEGROUP, value);
    }

    /**
     * Gets the attribute value for the calculated attribute LastRefreshDate.
     * @return the LastRefreshDate
     */
    public Date getLastRefreshDate() {
        return (Date) getAttributeInternal(LASTREFRESHDATE);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute LastRefreshDate.
     * @param value value to set the  LastRefreshDate
     */
    public void setLastRefreshDate(Date value) {
        setAttributeInternal(LASTREFRESHDATE, value);
    }

    /**
     * Gets the attribute value for the calculated attribute WorkflowState.
     * @return the WorkflowState
     */
    public String getWorkflowState() {
        return (String) getAttributeInternal(WORKFLOWSTATE);
    }

    /**
     * Sets <code>value</code> as the attribute value for the calculated attribute WorkflowState.
     * @param value value to set the  WorkflowState
     */
    public void setWorkflowState(String value) {
        setAttributeInternal(WORKFLOWSTATE, value);
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
