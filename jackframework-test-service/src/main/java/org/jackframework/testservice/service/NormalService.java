package org.jackframework.testservice.service;

import com.github.pagehelper.PageHelper;
import org.jackframework.common.exceptions.RunningException;
import org.jackframework.jdbc.core.CommonDao;
import org.jackframework.jdbc.core.Excludes;
import org.jackframework.jdbc.core.Includes;
import org.jackframework.service.annotation.EndService;
import org.jackframework.service.annotation.Publish;
import org.jackframework.service.component.ServiceSessionHolder;
import org.jackframework.testservice.mapper.NormalMapper;
import org.jackframework.testservice.pojo.TData;
import org.serviceframework.component.Pagination;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@EndService
@Publish("/api/normal")
public class NormalService {

    @Autowired
    protected NormalMapper normalMapper;

    @Autowired
    protected CommonDao commonDao;

    /**
     * Hello
     */
    @Publish("/hello")
    public String hello() {
        return "Hello Normal Service";
    }

    /**
     * 新增数据
     */
    @Publish("/insertData")
    @Transactional
    public long insertData(String dataString, Integer dataInt, BigDecimal dataDecimal,
                           Date dataDate, Date dataDatetime, Boolean dataBoolean) {
        TData data = createData(null, dataString, dataInt, dataDecimal, dataDate, dataDatetime, dataBoolean);
        commonDao.insert(data);
        return data.getDataId();
    }

    /**
     * 根据ID查询（全部字段）
     */
    @Publish("/findDataById")
    public TData findDataById(Long dataId) {
        return commonDao.findOne(TData.class, dataId);
    }

    /**
     * 根据ID查询(包含指定字段)
     */
    @Publish("/findDataIncludeById")
    public TData findDataIncludeById(Long dataId) {
        return commonDao.findOne(TData.class, dataId, Includes.include("dataString", "dataInt"));
    }

    /**
     * 根据ID查询(不包含指定字段)
     */
    @Publish("/findDataExcludeById")
    public TData findDataExcludeById(Long dataId) {
        return commonDao.findOne(TData.class, dataId, Excludes.excludes("dataDatetime", "dataDate"));
    }

    /**
     * 根据ID查询(单个字段)
     */
    @Publish("/findDataStringById")
    public String findDataStringById(Long dataId) {
        return commonDao.findField(TData.class, "dataString", dataId);
    }

    /**
     * 条件查询（包含指定字段）
     */
    @Publish("/findDataBetweenInclude")
    public List<TData> findDataBetweenInclude(Date beginDate, Date endDate) {
        return commonDao.findList(
                TData.class,
                Includes.include("dataDate", "dataInt"),
                "where data_date >= ? and data_date <= ?", beginDate, endDate);
    }

    /**
     * 条件查询（不包含指定字段）
     */
    @Publish("/findDataBetweenExclude")
    public List<TData> findDataBetweenExclude(Date beginDate, Date endDate) {
        return commonDao.findList(
                TData.class,
                Excludes.excludes("dataInt"),
                "where data_date >= ? and data_date <= ?", beginDate, endDate);
    }

    /**
     * 条件查询（返回MapList）
     */
    @Publish("/findDataMapList")
    public List<Map<String, Object>> findDataMapList() {
        return commonDao.findMapList(TData.class, "1=1");
    }

    /**
     * 条件查询（返回单个Map）
     */
    @Publish("/findDataMap")
    public Map<String, Object> findDataMap(String dataId) {
        return commonDao.findMap(TData.class, "where data_id=?", dataId);
    }

    /**
     * 分页查询
     */
    @Publish("/findDataPage")
    public Pagination<TData> findDataPage(Integer pageNum) {
        PageHelper.startPage(pageNum == null ? 1 : pageNum, 10);
        return Pagination.toPagination(normalMapper.findDataPage());
    }

    /**
     * 修改数据的所有字段
     */
    @Publish("/updateData")
    @Transactional
    public int updateData(Long dataId, String dataString, Integer dataInt, BigDecimal dataDecimal,
                          Date dataDate, Date dataDatetime, Boolean dataBoolean) {
        return commonDao.update(
                createData(dataId, dataString, dataInt, dataDecimal, dataDate, dataDatetime, dataBoolean));
    }

    /**
     * 修改数据的非空字段
     */
    @Publish("/updateDataOptimized")
    @Transactional
    public int updateDataOptimized(Long dataId, String dataString, Integer dataInt, BigDecimal dataDecimal,
                                   Date dataDate, Date dataDatetime, Boolean dataBoolean) {
        return commonDao.updateOptimized(
                createData(dataId, dataString, dataInt, dataDecimal, dataDate, dataDatetime, dataBoolean));
    }

    /**
     * 修改数据的非空字段（dataString、dataDatetime 除外）
     */
    @Publish("/updateDataOptimizedWithForce")
    @Transactional
    public int updateDataOptimizedWithForce(Long dataId, String dataString, Integer dataInt, BigDecimal dataDecimal,
                                            Date dataDate, Date dataDatetime, Boolean dataBoolean) {
        return commonDao.updateOptimized(
                createData(dataId, dataString, dataInt, dataDecimal, dataDate, dataDatetime, dataBoolean),
                "dataString", "dataDatetime");
    }

    /**
     * 修改所有数据的dataString字段
     */
    @Publish("/updateAllString")
    @Transactional
    public int updateAllString(String dataString) {
        return commonDao.update(TData.class, "set data_string = ?", dataString);
    }

    /**
     * 根据ID删除数据
     */
    @Publish("/deleteDataById")
    @Transactional
    public int deleteDataById(Long dataId) {
        return commonDao.delete(TData.class, dataId);
    }

    /**
     * 删除指定条件的数据
     */
    @Publish("/deleteDataBetween")
    @Transactional
    public int deleteBetween(Date beginDate, Date endDate) {
        return commonDao.delete(TData.class, "data_datetime between ? and ?", beginDate, endDate);
    }

    /**
     * 测试事物回滚
     */
    @Publish("/transactionRollback")
    @Transactional
    public void transactionRollback(Integer dataInt, Boolean rollback) {
        commonDao.update(TData.class, "set data_int=?", dataInt);
        if (rollback != null && rollback) {
            throw new RunningException("Transaction is rollback.");
        }
    }

    /**
     * 设置Session值
     */
    @Publish("/setSession")
    public void setSession(String name, String value) {
        ServiceSessionHolder.getSession().setAttribute(name, value);
    }

    /**
     * 获取Session值
     */
    @Publish("/getSession")
    public Object setSession(String name) {
        return ServiceSessionHolder.getSession().getAttribute(name);
    }

    protected TData createData(Long dataId, String dataString, Integer dataInt, BigDecimal dataDecimal,
                               Date dataDate, Date dataDatetime, Boolean dataBoolean) {
        TData data = new TData();
        data.setDataId(dataId);
        data.setDataString(dataString);
        data.setDataInt(dataInt);
        data.setDataDecimal(dataDecimal);
        data.setDataDate(dataDate);
        data.setDataDatetime(dataDatetime);
        data.setDataBoolean(dataBoolean);
        return data;
    }

}
