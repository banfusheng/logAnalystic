/**
 * 根据各个维度对象获取对应维度的id接口
 *
 * @author Administrator
 * @create 2018/8/21
 * @since 1.0.0
 */
package com.qf.anlystic.service;

import com.qf.anlystic.model.dim.base.BaseDimension;
import com.qf.anlystic.model.dim.value.BaseStatsValueWritable;

public interface IDimensionConvert {
    /**
     *  根据维度对象来获取对应维度的id接口
     */

    int getDimensionIDByDimension(BaseDimension baseDimension);

    int getDimensionIDByDimension(BaseStatsValueWritable baseStatsValueWritable);


}
