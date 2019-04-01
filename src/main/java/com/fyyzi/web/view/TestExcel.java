package com.fyyzi.web.view;

import com.fyyzi.common.utils.AbstractSimpleExcel;
import com.fyyzi.entity.User;

import java.util.List;

public class TestExcel extends AbstractSimpleExcel<User> {

    /**
     * @param fileName  文件名
     * @param sheetName sheet页名
     * @param bodyList  body体List集合
     */
    public TestExcel(String fileName, String sheetName, List<User> bodyList) {
        super(fileName, sheetName, bodyList);
    }

    @Override
    public List<String> createHeaderCellValueList(List<String> cellValues) {
        cellValues.add("123");
        return null;
    }

    @Override
    public List<Object> createBodyCellValuesList(List<Object> cellValues, User model) {
        cellValues.add(model.getPeople());
        return null;
    }
}
