package org.lulzm.waft;


public class CardItem {
    private String tv_QR;
    int viewType;

    public CardItem(String tv_QR) {
        this.tv_QR = tv_QR;
        this.viewType = viewType;
    }

    public String getTv_QR() {
        return tv_QR;
    }

    public int getViewType() {
        return viewType;
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public void setTv_QR(String tv_QR) {
        this.tv_QR = tv_QR;
    }
}
