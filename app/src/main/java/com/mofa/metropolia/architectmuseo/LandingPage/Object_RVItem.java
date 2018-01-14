package com.mofa.metropolia.architectmuseo.LandingPage;


class Object_RVItem {
    private String img_base64;
    private String cateName;

    Object_RVItem(String cate, String img_base64) {
        this.img_base64 = img_base64;
        this.cateName = cate;
    }

    String getImage64() {
        return img_base64;
    }

    String getCateName() {
        return cateName;
    }
}
