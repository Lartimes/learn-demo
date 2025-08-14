package org.bankai.common;

import java.util.List;


/**
 * 单元格封装基类
 * 1.图片
 * 2.公式
 * 3.列表
 * 4.单元格
 */
public class CellContent extends DocumentBaseContent {
    private List<Image> images ;
    private List<EquationInfo> equations;
    private List<ListInfo> lists;
    private List<CellContent[][]> nestedTables; // 支持多层嵌套

}
