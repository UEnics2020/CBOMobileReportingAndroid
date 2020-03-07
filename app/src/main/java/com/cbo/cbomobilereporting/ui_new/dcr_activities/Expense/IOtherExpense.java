package com.cbo.cbomobilereporting.ui_new.dcr_activities.Expense;

import com.cbo.cbomobilereporting.ui_new.dcr_activities.Expense.newotherExp.MPopupData;

import java.util.ArrayList;

public interface IOtherExpense {
    String getCompanyCode();

    String getDCRId();

    String getUserId();

    void getReferencesById();

    void setTitle();

    void KmLayoutRequired(Boolean required);

    void loadExpenseHead(ArrayList<mExpHead> expHeads);

    void addAttachment();

    //void setExpenseHead(mExpHead expenseHead);
    void setRemarkCaption(String caption);

    void setAmountCaption(String caption);

    void setAmtHint(String hint);

    void setRemark(String remark);

    void setAmount(Double amount);

    void setKm(Double km);

    void setRate(Double rate);

    void setAttachment(ArrayList<String> path);

    void onSendResponse(mOthExpense othExpense);

    void onUpdateArea(ArrayList<MPopupData> fromArea);

    void onUpdateExpType(ArrayList<MPopupData> expTyp);

    void loadExpenseType(ArrayList<MPopupData> expTyp);

    void onUpdateView(String ID, String DCR_ID, String FROM_STATION, String TO_STATION, String EXP_TYPE, String FARE, String KM, String AMOUNT, String REMARK, String FILE_NAME);
}
