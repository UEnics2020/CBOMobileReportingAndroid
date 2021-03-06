package utils.adapterutils;

import java.io.Serializable;

public class PobModel implements Serializable{

    private String name = "";
    private String id = "";
    private String rate = "";
    private String noc = "";
    private boolean selected = false;
    private String score = "";
    private String dr_item = "1";
    private String pob = "";
    private boolean selected_Rx = false;
    private String Rx_Qty = "";
    private boolean highlight=false;
    private int Stock = 0;
    private int Balance = 0;
    private int SPL_ID = 0;

    public PobModel(String name, String id, String rate) {
        this.name = name;
        this.id = id;
        this.rate = rate;
        selected = false;
        selected_Rx=false;
        score = "";
    }

    public PobModel(String name, String id, String rate,String dr_item,int Stock,int Balance,int SPL_ID) {
        this.name = name;
        this.id = id;
        this.rate = rate;
        selected = false;
        selected_Rx=false;
        score = "";
        this.dr_item=dr_item;
        this.Stock = Stock;
        this.Balance = Balance;
        this.SPL_ID = SPL_ID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void setSelected_Rx(boolean selected_Rx) {
        this.selected_Rx = selected_Rx;
    }

    public boolean isSelected_Rx() {
        return selected_Rx;
    }

    public String getRx_Qty() {
        return Rx_Qty;
    }

    public void setRx_Qty(String rx_Qty) {
        Rx_Qty = rx_Qty;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getPob() {
        return pob.trim().isEmpty()?"0":pob.trim();
    }
    public boolean isHighlighted() {
        return highlight;
    }

    public void setHighlight(boolean highlight) {
        this.highlight = highlight;
    }

    public String isdr_item() {
        return dr_item;
    }

    public void setdr_item(String dr_item) {
        this.dr_item = dr_item;
    }

    public void setPob(String pob) {
        this.pob = pob;
    }

    public String getRate() {
        return rate.trim().isEmpty()?"0":rate.trim();
    }

    public void setRate(String rate) {
        this.rate = rate;

    }

    public String getNOC() {
        return noc;
    }

    public void setNOC(String noc) {
        this.noc = noc;

    }

    public int getStock() {
        return Stock;
    }

    public void setStock(int stock) {
        Stock = stock;
    }

    public int getBalance() {
        return Balance;
    }

    public void setBalance(int balance) {
        Balance = balance;
    }

    public int getSPL_ID() {
        return SPL_ID;
    }

    public void setSPL_ID(int SPL_ID) {
        this.SPL_ID = SPL_ID;
    }
}
