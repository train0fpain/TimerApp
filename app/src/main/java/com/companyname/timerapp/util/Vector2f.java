package com.companyname.timerapp.util;

public class Vector2f {
    public float x;
    public float y;

    public Vector2f() {
    }

    public Vector2f(Vector2f vec){
        this.x = vec.x;
        this.y = vec.y;
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2f add(Vector2f vec1, Vector2f vec2){
        Vector2f localVec = new Vector2f(vec1);
        return localVec.add(vec2);
    }

    public Vector2f add(Vector2f other){
        this.x += other.x;
        this.y += other.y;
        return this;
    }

    public static Vector2f sub(Vector2f vec1, Vector2f vec2){
        Vector2f localVec = new Vector2f(vec1);
        return localVec.sub(vec2);
    }

    public Vector2f sub(Vector2f other){
        this.x -= other.x;
        this.y -= other.y;
        return this;
    }

    public static Vector2f scale(Vector2f vec1, float scale){
        Vector2f localVec = new Vector2f(vec1);;
        return localVec.scale(scale);
    }

    public Vector2f scale(float val){
        this.x *= val;
        this.y *= val;
        return this;
    }

    public float length(){
        return (float) Math.sqrt(Math.pow(this.x, 2d) + Math.pow(this.y, 2d));
    }

    public static Vector2f normalize(Vector2f vec1){
        Vector2f localVec = new Vector2f(vec1);;
        return localVec.normalize();
    }

    public Vector2f normalize(){
        float length = this.length();
        if (length > 0) {
            return this.scale(1f / this.length());
        }else{
            this.x = 0;
            this.y = 0;
            return this;
        }
    }
}
