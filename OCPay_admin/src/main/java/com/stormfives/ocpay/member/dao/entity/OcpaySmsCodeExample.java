package com.stormfives.ocpay.member.dao.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class OcpaySmsCodeExample {
    protected String orderByClause;

    protected boolean distinct;

    protected List<Criteria> oredCriteria;

    public OcpaySmsCodeExample() {
        oredCriteria = new ArrayList<Criteria>();
    }

    public void setOrderByClause(String orderByClause) {
        this.orderByClause = orderByClause;
    }

    public String getOrderByClause() {
        return orderByClause;
    }

    public void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    public boolean isDistinct() {
        return distinct;
    }

    public List<Criteria> getOredCriteria() {
        return oredCriteria;
    }

    public void or(Criteria criteria) {
        oredCriteria.add(criteria);
    }

    public Criteria or() {
        Criteria criteria = createCriteriaInternal();
        oredCriteria.add(criteria);
        return criteria;
    }

    public Criteria createCriteria() {
        Criteria criteria = createCriteriaInternal();
        if (oredCriteria.size() == 0) {
            oredCriteria.add(criteria);
        }
        return criteria;
    }

    protected Criteria createCriteriaInternal() {
        Criteria criteria = new Criteria();
        return criteria;
    }

    public void clear() {
        oredCriteria.clear();
        orderByClause = null;
        distinct = false;
    }

    protected abstract static class GeneratedCriteria {
        protected List<Criterion> criteria;

        protected GeneratedCriteria() {
            super();
            criteria = new ArrayList<Criterion>();
        }

        public boolean isValid() {
            return criteria.size() > 0;
        }

        public List<Criterion> getAllCriteria() {
            return criteria;
        }

        public List<Criterion> getCriteria() {
            return criteria;
        }

        protected void addCriterion(String condition) {
            if (condition == null) {
                throw new RuntimeException("Value for condition cannot be null");
            }
            criteria.add(new Criterion(condition));
        }

        protected void addCriterion(String condition, Object value, String property) {
            if (value == null) {
                throw new RuntimeException("Value for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value));
        }

        protected void addCriterion(String condition, Object value1, Object value2, String property) {
            if (value1 == null || value2 == null) {
                throw new RuntimeException("Between values for " + property + " cannot be null");
            }
            criteria.add(new Criterion(condition, value1, value2));
        }

        public Criteria andEscidIsNull() {
            addCriterion("escid is null");
            return (Criteria) this;
        }

        public Criteria andEscidIsNotNull() {
            addCriterion("escid is not null");
            return (Criteria) this;
        }

        public Criteria andEscidEqualTo(Integer value) {
            addCriterion("escid =", value, "escid");
            return (Criteria) this;
        }

        public Criteria andEscidNotEqualTo(Integer value) {
            addCriterion("escid <>", value, "escid");
            return (Criteria) this;
        }

        public Criteria andEscidGreaterThan(Integer value) {
            addCriterion("escid >", value, "escid");
            return (Criteria) this;
        }

        public Criteria andEscidGreaterThanOrEqualTo(Integer value) {
            addCriterion("escid >=", value, "escid");
            return (Criteria) this;
        }

        public Criteria andEscidLessThan(Integer value) {
            addCriterion("escid <", value, "escid");
            return (Criteria) this;
        }

        public Criteria andEscidLessThanOrEqualTo(Integer value) {
            addCriterion("escid <=", value, "escid");
            return (Criteria) this;
        }

        public Criteria andEscidIn(List<Integer> values) {
            addCriterion("escid in", values, "escid");
            return (Criteria) this;
        }

        public Criteria andEscidNotIn(List<Integer> values) {
            addCriterion("escid not in", values, "escid");
            return (Criteria) this;
        }

        public Criteria andEscidBetween(Integer value1, Integer value2) {
            addCriterion("escid between", value1, value2, "escid");
            return (Criteria) this;
        }

        public Criteria andEscidNotBetween(Integer value1, Integer value2) {
            addCriterion("escid not between", value1, value2, "escid");
            return (Criteria) this;
        }

        public Criteria andEsccodeIsNull() {
            addCriterion("esccode is null");
            return (Criteria) this;
        }

        public Criteria andEsccodeIsNotNull() {
            addCriterion("esccode is not null");
            return (Criteria) this;
        }

        public Criteria andEsccodeEqualTo(String value) {
            addCriterion("esccode =", value, "esccode");
            return (Criteria) this;
        }

        public Criteria andEsccodeNotEqualTo(String value) {
            addCriterion("esccode <>", value, "esccode");
            return (Criteria) this;
        }

        public Criteria andEsccodeGreaterThan(String value) {
            addCriterion("esccode >", value, "esccode");
            return (Criteria) this;
        }

        public Criteria andEsccodeGreaterThanOrEqualTo(String value) {
            addCriterion("esccode >=", value, "esccode");
            return (Criteria) this;
        }

        public Criteria andEsccodeLessThan(String value) {
            addCriterion("esccode <", value, "esccode");
            return (Criteria) this;
        }

        public Criteria andEsccodeLessThanOrEqualTo(String value) {
            addCriterion("esccode <=", value, "esccode");
            return (Criteria) this;
        }

        public Criteria andEsccodeLike(String value) {
            addCriterion("esccode like", value, "esccode");
            return (Criteria) this;
        }

        public Criteria andEsccodeNotLike(String value) {
            addCriterion("esccode not like", value, "esccode");
            return (Criteria) this;
        }

        public Criteria andEsccodeIn(List<String> values) {
            addCriterion("esccode in", values, "esccode");
            return (Criteria) this;
        }

        public Criteria andEsccodeNotIn(List<String> values) {
            addCriterion("esccode not in", values, "esccode");
            return (Criteria) this;
        }

        public Criteria andEsccodeBetween(String value1, String value2) {
            addCriterion("esccode between", value1, value2, "esccode");
            return (Criteria) this;
        }

        public Criteria andEsccodeNotBetween(String value1, String value2) {
            addCriterion("esccode not between", value1, value2, "esccode");
            return (Criteria) this;
        }

        public Criteria andEscphoneIsNull() {
            addCriterion("escphone is null");
            return (Criteria) this;
        }

        public Criteria andEscphoneIsNotNull() {
            addCriterion("escphone is not null");
            return (Criteria) this;
        }

        public Criteria andEscphoneEqualTo(String value) {
            addCriterion("escphone =", value, "escphone");
            return (Criteria) this;
        }

        public Criteria andEscphoneNotEqualTo(String value) {
            addCriterion("escphone <>", value, "escphone");
            return (Criteria) this;
        }

        public Criteria andEscphoneGreaterThan(String value) {
            addCriterion("escphone >", value, "escphone");
            return (Criteria) this;
        }

        public Criteria andEscphoneGreaterThanOrEqualTo(String value) {
            addCriterion("escphone >=", value, "escphone");
            return (Criteria) this;
        }

        public Criteria andEscphoneLessThan(String value) {
            addCriterion("escphone <", value, "escphone");
            return (Criteria) this;
        }

        public Criteria andEscphoneLessThanOrEqualTo(String value) {
            addCriterion("escphone <=", value, "escphone");
            return (Criteria) this;
        }

        public Criteria andEscphoneLike(String value) {
            addCriterion("escphone like", value, "escphone");
            return (Criteria) this;
        }

        public Criteria andEscphoneNotLike(String value) {
            addCriterion("escphone not like", value, "escphone");
            return (Criteria) this;
        }

        public Criteria andEscphoneIn(List<String> values) {
            addCriterion("escphone in", values, "escphone");
            return (Criteria) this;
        }

        public Criteria andEscphoneNotIn(List<String> values) {
            addCriterion("escphone not in", values, "escphone");
            return (Criteria) this;
        }

        public Criteria andEscphoneBetween(String value1, String value2) {
            addCriterion("escphone between", value1, value2, "escphone");
            return (Criteria) this;
        }

        public Criteria andEscphoneNotBetween(String value1, String value2) {
            addCriterion("escphone not between", value1, value2, "escphone");
            return (Criteria) this;
        }

        public Criteria andEsccreatedateIsNull() {
            addCriterion("esccreatedate is null");
            return (Criteria) this;
        }

        public Criteria andEsccreatedateIsNotNull() {
            addCriterion("esccreatedate is not null");
            return (Criteria) this;
        }

        public Criteria andEsccreatedateEqualTo(Date value) {
            addCriterion("esccreatedate =", value, "esccreatedate");
            return (Criteria) this;
        }

        public Criteria andEsccreatedateNotEqualTo(Date value) {
            addCriterion("esccreatedate <>", value, "esccreatedate");
            return (Criteria) this;
        }

        public Criteria andEsccreatedateGreaterThan(Date value) {
            addCriterion("esccreatedate >", value, "esccreatedate");
            return (Criteria) this;
        }

        public Criteria andEsccreatedateGreaterThanOrEqualTo(Date value) {
            addCriterion("esccreatedate >=", value, "esccreatedate");
            return (Criteria) this;
        }

        public Criteria andEsccreatedateLessThan(Date value) {
            addCriterion("esccreatedate <", value, "esccreatedate");
            return (Criteria) this;
        }

        public Criteria andEsccreatedateLessThanOrEqualTo(Date value) {
            addCriterion("esccreatedate <=", value, "esccreatedate");
            return (Criteria) this;
        }

        public Criteria andEsccreatedateIn(List<Date> values) {
            addCriterion("esccreatedate in", values, "esccreatedate");
            return (Criteria) this;
        }

        public Criteria andEsccreatedateNotIn(List<Date> values) {
            addCriterion("esccreatedate not in", values, "esccreatedate");
            return (Criteria) this;
        }

        public Criteria andEsccreatedateBetween(Date value1, Date value2) {
            addCriterion("esccreatedate between", value1, value2, "esccreatedate");
            return (Criteria) this;
        }

        public Criteria andEsccreatedateNotBetween(Date value1, Date value2) {
            addCriterion("esccreatedate not between", value1, value2, "esccreatedate");
            return (Criteria) this;
        }

        public Criteria andEscexpiredateIsNull() {
            addCriterion("escexpiredate is null");
            return (Criteria) this;
        }

        public Criteria andEscexpiredateIsNotNull() {
            addCriterion("escexpiredate is not null");
            return (Criteria) this;
        }

        public Criteria andEscexpiredateEqualTo(Date value) {
            addCriterion("escexpiredate =", value, "escexpiredate");
            return (Criteria) this;
        }

        public Criteria andEscexpiredateNotEqualTo(Date value) {
            addCriterion("escexpiredate <>", value, "escexpiredate");
            return (Criteria) this;
        }

        public Criteria andEscexpiredateGreaterThan(Date value) {
            addCriterion("escexpiredate >", value, "escexpiredate");
            return (Criteria) this;
        }

        public Criteria andEscexpiredateGreaterThanOrEqualTo(Date value) {
            addCriterion("escexpiredate >=", value, "escexpiredate");
            return (Criteria) this;
        }

        public Criteria andEscexpiredateLessThan(Date value) {
            addCriterion("escexpiredate <", value, "escexpiredate");
            return (Criteria) this;
        }

        public Criteria andEscexpiredateLessThanOrEqualTo(Date value) {
            addCriterion("escexpiredate <=", value, "escexpiredate");
            return (Criteria) this;
        }

        public Criteria andEscexpiredateIn(List<Date> values) {
            addCriterion("escexpiredate in", values, "escexpiredate");
            return (Criteria) this;
        }

        public Criteria andEscexpiredateNotIn(List<Date> values) {
            addCriterion("escexpiredate not in", values, "escexpiredate");
            return (Criteria) this;
        }

        public Criteria andEscexpiredateBetween(Date value1, Date value2) {
            addCriterion("escexpiredate between", value1, value2, "escexpiredate");
            return (Criteria) this;
        }

        public Criteria andEscexpiredateNotBetween(Date value1, Date value2) {
            addCriterion("escexpiredate not between", value1, value2, "escexpiredate");
            return (Criteria) this;
        }

        public Criteria andEscvalidIsNull() {
            addCriterion("escvalid is null");
            return (Criteria) this;
        }

        public Criteria andEscvalidIsNotNull() {
            addCriterion("escvalid is not null");
            return (Criteria) this;
        }

        public Criteria andEscvalidEqualTo(Integer value) {
            addCriterion("escvalid =", value, "escvalid");
            return (Criteria) this;
        }

        public Criteria andEscvalidNotEqualTo(Integer value) {
            addCriterion("escvalid <>", value, "escvalid");
            return (Criteria) this;
        }

        public Criteria andEscvalidGreaterThan(Integer value) {
            addCriterion("escvalid >", value, "escvalid");
            return (Criteria) this;
        }

        public Criteria andEscvalidGreaterThanOrEqualTo(Integer value) {
            addCriterion("escvalid >=", value, "escvalid");
            return (Criteria) this;
        }

        public Criteria andEscvalidLessThan(Integer value) {
            addCriterion("escvalid <", value, "escvalid");
            return (Criteria) this;
        }

        public Criteria andEscvalidLessThanOrEqualTo(Integer value) {
            addCriterion("escvalid <=", value, "escvalid");
            return (Criteria) this;
        }

        public Criteria andEscvalidIn(List<Integer> values) {
            addCriterion("escvalid in", values, "escvalid");
            return (Criteria) this;
        }

        public Criteria andEscvalidNotIn(List<Integer> values) {
            addCriterion("escvalid not in", values, "escvalid");
            return (Criteria) this;
        }

        public Criteria andEscvalidBetween(Integer value1, Integer value2) {
            addCriterion("escvalid between", value1, value2, "escvalid");
            return (Criteria) this;
        }

        public Criteria andEscvalidNotBetween(Integer value1, Integer value2) {
            addCriterion("escvalid not between", value1, value2, "escvalid");
            return (Criteria) this;
        }

        public Criteria andEscphonepreIsNull() {
            addCriterion("escphonepre is null");
            return (Criteria) this;
        }

        public Criteria andEscphonepreIsNotNull() {
            addCriterion("escphonepre is not null");
            return (Criteria) this;
        }

        public Criteria andEscphonepreEqualTo(String value) {
            addCriterion("escphonepre =", value, "escphonepre");
            return (Criteria) this;
        }

        public Criteria andEscphonepreNotEqualTo(String value) {
            addCriterion("escphonepre <>", value, "escphonepre");
            return (Criteria) this;
        }

        public Criteria andEscphonepreGreaterThan(String value) {
            addCriterion("escphonepre >", value, "escphonepre");
            return (Criteria) this;
        }

        public Criteria andEscphonepreGreaterThanOrEqualTo(String value) {
            addCriterion("escphonepre >=", value, "escphonepre");
            return (Criteria) this;
        }

        public Criteria andEscphonepreLessThan(String value) {
            addCriterion("escphonepre <", value, "escphonepre");
            return (Criteria) this;
        }

        public Criteria andEscphonepreLessThanOrEqualTo(String value) {
            addCriterion("escphonepre <=", value, "escphonepre");
            return (Criteria) this;
        }

        public Criteria andEscphonepreLike(String value) {
            addCriterion("escphonepre like", value, "escphonepre");
            return (Criteria) this;
        }

        public Criteria andEscphonepreNotLike(String value) {
            addCriterion("escphonepre not like", value, "escphonepre");
            return (Criteria) this;
        }

        public Criteria andEscphonepreIn(List<String> values) {
            addCriterion("escphonepre in", values, "escphonepre");
            return (Criteria) this;
        }

        public Criteria andEscphonepreNotIn(List<String> values) {
            addCriterion("escphonepre not in", values, "escphonepre");
            return (Criteria) this;
        }

        public Criteria andEscphonepreBetween(String value1, String value2) {
            addCriterion("escphonepre between", value1, value2, "escphonepre");
            return (Criteria) this;
        }

        public Criteria andEscphonepreNotBetween(String value1, String value2) {
            addCriterion("escphonepre not between", value1, value2, "escphonepre");
            return (Criteria) this;
        }
    }

    public static class Criteria extends GeneratedCriteria {

        protected Criteria() {
            super();
        }
    }

    public static class Criterion {
        private String condition;

        private Object value;

        private Object secondValue;

        private boolean noValue;

        private boolean singleValue;

        private boolean betweenValue;

        private boolean listValue;

        private String typeHandler;

        public String getCondition() {
            return condition;
        }

        public Object getValue() {
            return value;
        }

        public Object getSecondValue() {
            return secondValue;
        }

        public boolean isNoValue() {
            return noValue;
        }

        public boolean isSingleValue() {
            return singleValue;
        }

        public boolean isBetweenValue() {
            return betweenValue;
        }

        public boolean isListValue() {
            return listValue;
        }

        public String getTypeHandler() {
            return typeHandler;
        }

        protected Criterion(String condition) {
            super();
            this.condition = condition;
            this.typeHandler = null;
            this.noValue = true;
        }

        protected Criterion(String condition, Object value, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.typeHandler = typeHandler;
            if (value instanceof List<?>) {
                this.listValue = true;
            } else {
                this.singleValue = true;
            }
        }

        protected Criterion(String condition, Object value) {
            this(condition, value, null);
        }

        protected Criterion(String condition, Object value, Object secondValue, String typeHandler) {
            super();
            this.condition = condition;
            this.value = value;
            this.secondValue = secondValue;
            this.typeHandler = typeHandler;
            this.betweenValue = true;
        }

        protected Criterion(String condition, Object value, Object secondValue) {
            this(condition, value, secondValue, null);
        }
    }
}