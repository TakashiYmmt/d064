package jp.co.webshark.d064_alpha;

/**
 * Created by takashi on 2017/09/25.
 */
public class ProductData {
    String		productName;
    String		productImageUrl;
    String		productLinkUrl;
    String 		snsText1;
    String 		snsText2;
    String 		id;
    String 		reward;

    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getProductLinkUrl() {
        return productLinkUrl;
    }
    public void setProductLinkUrl(String productLinkUrl) {
        this.productLinkUrl = productLinkUrl;
    }
    public String getProductImageUrl() {
        return productImageUrl;
    }
    public void setProductImageUrl(String productImageUrl) {
        this.productImageUrl = productImageUrl;
    }
    public String getSnsText1() {
        return snsText1;
    }
    public void setSnsText1(String snsText1) {
        this.snsText1 = snsText1;
    }
    public String getSnsText2() {
        return snsText2;
    }
    public void setSnsText2(String snsText2) {
        this.snsText2 = snsText2;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getReward() {
        return reward;
    }
    public void setReward(String reward) {
        this.reward = reward;
    }
}
