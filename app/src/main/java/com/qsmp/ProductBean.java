package com.qsmp;

import java.util.Objects;

public class ProductBean {

    String id,imageurl,name;
    int img_res;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getImg_res() {
        return img_res;
    }

    public void setImg_res(int img_res) {
        this.img_res = img_res;
    }
    public ProductBean()
    {

    }

    public ProductBean(String id, String imageurl, String name, int img_res) {
        this.id = id;
        this.imageurl = imageurl;
        this.name = name;

    }


    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        ProductBean nimPlayer = (ProductBean) o;
        return Objects.equals(id, nimPlayer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    /*@Override
    public boolean equals(Object v) {
        boolean retVal = false;

        if (v instanceof ProductBean){
            ProductBean ptr = (ProductBean) v;
            retVal = Objects.equals(ptr.id, this.id);
        }

        return retVal;
    }*/
}
