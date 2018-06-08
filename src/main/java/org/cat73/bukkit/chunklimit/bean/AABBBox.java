package org.cat73.bukkit.chunklimit.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * AABB 盒子
 */
@Data
@Accessors(chain = true)
@AllArgsConstructor
public class AABBBox {
    /**
     * 左下角横坐标
     */
    private final int x1;
    /**
     * 左下角纵坐标
     */
    private final int z1;
    /**
     * 右上角横坐标
     */
    private final int x2;
    /**
     * 右上角纵坐标
     */
    private final int z2;

    /**
     * 判断一个坐标是否在盒子的范围内
     * @param x 横坐标
     * @param z 纵坐标
     * @return 输入坐标是否在盒子的范围内
     */
    public boolean contains(int x, int z) {
        return x >= x1 && x <= x2 && z >= z1 && z <= z2;
    }
}
