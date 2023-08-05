package top.htext.botools.config;

import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;

public class BotConfig {
    private String name;
    private Vec3d pos;
    private Vec2f rotation;
    private Identifier dimension;
    private String info;

    public BotConfig(String name, Vec3d pos, Vec2f rotation, Identifier dimension, String info) {
        this.name = name;
        this.pos = pos;
        this.rotation = rotation;
        this.dimension = dimension;
        this.info = info;
    }

    public Vec2f getRotation() {
        return rotation;
    }

    public Vec3d getPos() {
        return pos;
    }

    public String getName() {
        return name;
    }

    public String getInfo() {
        return info;
    }

    public Identifier getDimension() {
        return dimension;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPos(Vec3d pos) {
        this.pos = pos;
    }

    public void setRotation(Vec2f rotation) {
        this.rotation = rotation;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public void setDimension(Identifier dimension) {
        this.dimension = dimension;
    }
}
