package com.example.heartsvalentine;

public enum ShapeType {
    None,
    StraightHeart,
    ConvexHeart,  // fat heart
    ConcaveHeart,  // thin heart
    Circle,
    Star,
    Square,
    FlatRectangle,
    TallRectangle,
    FlatOval,
    TallOval,
    WaterDrop,
    Lozenge,
    Spade,
    Club,
    Diamond
}
/*

public enum ShapeType {
    None(null),
    StraightHeart("Straight Heart"),
    ConvexHeart("Convex Heart"),  // fat heart
    ConcaveHeart("Concave Heart"),  // thin heart
    Circle("Circle"),
    Star("Star"),
    Square("Square"),
    FlatRectangle("Flat Rectangle"),
    TallRectangle("Tall Rectangle"),
    FlatOval("Flat Oval"),
    TallOval("Tall Oval"),
    WaterDrop("Water Drop"),
    Lozenge("Lozenge"),
    Spade("Spade"),
    Club("Tall Oval"),
    Diamond("Diamond");

    private String type;
    ShapeType(String type){
        this.type = type;
    }
    public String getTypeAsString(){
        return this.type;
    }
}

* */


