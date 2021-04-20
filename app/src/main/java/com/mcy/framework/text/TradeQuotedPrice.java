package com.mcy.framework.text;

import java.io.Serializable;

/**
 * Created by mcy on 2018/3/23.
 */

public class TradeQuotedPrice implements Serializable{

    /**
     * ID : 1000
     * PurchasePlanID : 1000
     * OperatorUserID : 100025
     * SupplierID : 1000
     * UnitPrice : 9.9
     * TotalPrice : 0
     * Quantity : 100
     * Description : 描述描述修改
     * ProductRegion : 来之黑龙江
     * QuotedTime : /Date(1493888862000+0800)/
     * Status : 1
     * StatusDestription : 状态说明
     */

    private int ID;
    private int PurchasePlanID;
    private int OperatorUserID;
    private int SupplierID;
    private double UnitPrice;
    private int TotalPrice;
    private int Quantity;
    private String Description;
    private String ProductRegion;
    private String QuotedTime;
    private int Status;
    private String StatusDestription;

    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getPurchasePlanID() {
        return PurchasePlanID;
    }

    public void setPurchasePlanID(int PurchasePlanID) {
        this.PurchasePlanID = PurchasePlanID;
    }

    public int getOperatorUserID() {
        return OperatorUserID;
    }

    public void setOperatorUserID(int OperatorUserID) {
        this.OperatorUserID = OperatorUserID;
    }

    public int getSupplierID() {
        return SupplierID;
    }

    public void setSupplierID(int SupplierID) {
        this.SupplierID = SupplierID;
    }

    public double getUnitPrice() {
        return UnitPrice;
    }

    public void setUnitPrice(double UnitPrice) {
        this.UnitPrice = UnitPrice;
    }

    public int getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(int TotalPrice) {
        this.TotalPrice = TotalPrice;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int Quantity) {
        this.Quantity = Quantity;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public String getProductRegion() {
        return ProductRegion;
    }

    public void setProductRegion(String ProductRegion) {
        this.ProductRegion = ProductRegion;
    }

    public String getQuotedTime() {
        return QuotedTime;
    }

    public void setQuotedTime(String QuotedTime) {
        this.QuotedTime = QuotedTime;
    }

    public int getStatus() {
        return Status;
    }

    public void setStatus(int Status) {
        this.Status = Status;
    }

    public String getStatusDestription() {
        return StatusDestription;
    }

    public void setStatusDestription(String StatusDestription) {
        this.StatusDestription = StatusDestription;
    }
}
