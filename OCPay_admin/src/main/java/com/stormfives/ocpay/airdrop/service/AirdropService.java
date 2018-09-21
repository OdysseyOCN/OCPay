package com.coinwallet.airdrop.service;

import com.coinwallet.airdrop.controller.req.AirdropReq;
import com.coinwallet.airdrop.controller.req.AirdropVo;
import com.coinwallet.airdrop.dao.*;
import com.coinwallet.airdrop.entity.*;
import com.coinwallet.common.domain.Page;
import com.coinwallet.common.exception.InvalidArgumentException;
import com.coinwallet.common.response.FailResponse;
import com.coinwallet.common.response.ResponseValue;
import com.coinwallet.common.response.SuccessResponse;
import com.coinwallet.common.util.Constants;
import com.coinwallet.common.util.DateUtils;
import com.github.pagehelper.PageHelper;
import com.ocpay.dao.EmailWalletAddressDAO;
import com.ocpay.dao.MemberDAO;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.web3j.utils.Convert;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by liuhuan on 2018/8/28.
 */
@Service
public class AirdropService {

    Logger logger = LoggerFactory.getLogger(AirdropService.class);


    @Autowired
    private AirdropInfoMapper airdropInfoMapper;

    @Autowired
    private EthService ethService;

    @Autowired
    private AirdropAccountInfoMapper airdropAccountInfoMapper;

    @Autowired
    private AirdropAccountInfoDAO airdropAccountInfoDAO;

    @Autowired
    private OcnAccountDAO ocnAccountDAO;


    @Autowired
    private MemberDAO memberDAO;
    @Autowired
    private EmailWalletAddressDAO emailWalletAddressDAO;

    @Autowired
    private AirdropTransactionOrderMapper airdropTransactionOrderMapper;


    /**
     * 导入空投excel
     *
     * @param file
     * @return
     * @throws InvalidArgumentException
     * @throws IOException
     */
    public ResponseValue importAirdrop(MultipartFile file) throws InvalidArgumentException, IOException {
        String filename = file.getOriginalFilename();
        InputStream inputStream = file.getInputStream();

        Workbook wb;


        if (isExcel2007(filename)) {
            wb = new XSSFWorkbook(inputStream);
        } else {
            wb = new HSSFWorkbook(inputStream);
        }


        //得到第一个shell
        Sheet sheet = wb.getSheetAt(0);
        //得到Excel的行数
        int totalRows = sheet.getPhysicalNumberOfRows();

        if (totalRows == 0) throw new InvalidArgumentException("上传文件不能没有内容!");
        Row rowFirst = sheet.getRow(0);
        if (rowFirst == null) throw new InvalidArgumentException("上传文件格式错误!首行不能为空,应为标题NAME!");

        Cell cellFirst = rowFirst.getCell(0);

        if (cellFirst == null) throw new InvalidArgumentException("上传文件格式错误!首列不能为空!");

        cellFirst.setCellType(Cell.CELL_TYPE_STRING);
        String cellValue = cellFirst.getStringCellValue();

        if (StringUtils.isBlank(cellValue)) throw new InvalidArgumentException("上传文件格式错误!首列不能为空!");
        if (!cellValue.equalsIgnoreCase("name")) throw new InvalidArgumentException("上传文件格式错误!首行应为标题Name!");


        int totalCells = 0;

        //得到Excel的列数(前提是有行数)
        if (totalRows >= 1 && sheet.getRow(0) != null) {
            totalCells = sheet.getRow(0).getPhysicalNumberOfCells();
        }
        List<AirdropInfo> airdropInfos = new ArrayList<>();
        //循环Excel行数,从第二行开始。标题不入库
        for (int r = 1; r < totalRows; r++) {
            Row row = sheet.getRow(r);
            if (row == null) continue;

            AirdropInfo airdropInfo = new AirdropInfo();


            airdropInfo.setCreateTime(new Date());
            airdropInfo.setUpdateTime(new Date());
            for (int c = 0; c < totalCells; c++) {
                Cell cell = row.getCell(c);
                cell.setCellType(Cell.CELL_TYPE_STRING);
                if (cell != null) {

                    if (c == 0) {
                        airdropInfo.setName(cell.getStringCellValue().toUpperCase());
                    } else if (c == 1) {
                        airdropInfo.setCoinName(cell.getStringCellValue().toUpperCase());
                    } else if (c == 2) {
                        airdropInfo.setAddress(cell.getStringCellValue());
                    } else if (c == 3) {
                        airdropInfo.setAmount(new BigDecimal(cell.getStringCellValue()));
                    }
                }
            }
            airdropInfos.add(airdropInfo);
        }

        if (airdropInfos.size() > 0) {
            airdropInfoMapper.addBatchAirdropInfo(airdropInfos);
            return new SuccessResponse("Successful!");
        }
        return new FailResponse("Failed!");
    }


    //@描述：是否是2007的excel，返回true是2007
    private static boolean isExcel2007(String filePath) {
        return filePath.matches("^.+\\.(?i)(xlsx)$");
    }

    /**
     * 空投列表
     *
     * @param airdropReq
     * @return
     */
    public ResponseValue getAirdrop(AirdropReq airdropReq) {
        PageHelper.startPage(airdropReq.getPageNum(), airdropReq.getPageSize());

        AirdropInfoExample airdropInfoExample = new AirdropInfoExample();

        AirdropInfoExample.Criteria criteria = airdropInfoExample.createCriteria();

        if (airdropReq.getId() != null) {
            criteria.andIdEqualTo(airdropReq.getId());
        }

        if (StringUtils.isNotBlank(airdropReq.getCoinName())) {
            criteria.andNameEqualTo(airdropReq.getCoinName());
        }

        if (airdropReq.getStatus() != null) {
            criteria.andStatusEqualTo(airdropReq.getStatus());
        }

        if (StringUtils.isNotBlank(airdropReq.getAddress())) {
            criteria.andAddressLike(airdropReq.getAddress() + "%");
        }

        List<AirdropInfo> airdropInfos = airdropInfoMapper.selectByExample(airdropInfoExample);
        return new SuccessResponse(Page.toPage(airdropInfos));
    }

    /**
     * 发放空投
     *
     * @return
     */
    public ResponseValue grantAirdrop(AirdropReq airdropReq) throws Exception {

        if (airdropReq.getId() == null) {
            return new FailResponse("空投账户ID不能为空");
        }

        AirdropAccountInfo airdropAccountInfo = getAirdropAccountInfo(airdropReq.getId());

        if (airdropAccountInfo == null) {
            return new FailResponse("空投账户不存在");
        }

        List<AirdropInfo> airdropInfos = getAirdropInfos(airdropAccountInfo.getName());

        if (airdropInfos == null || airdropInfos.size() <= 0) {
            return new FailResponse("没有可空投的信息");
        }
        BigInteger gasPrice = ethService.getGasPrice();
        gasPrice = new BigDecimal(gasPrice.toString()).add(Convert.toWei(new BigDecimal("1"), Convert.Unit.GWEI)).toBigInteger();
        logger.error("============空投需要的gasPrice======" + gasPrice);
        BigInteger gasLimit = Constants.AIRDROP_GASLIMIT;
        //需要空投的token数量
        BigDecimal airdropNum = airdropInfoMapper.selectAirdropNum(airdropAccountInfo.getName());
        //链上token余额
        BigDecimal tokenBalance = ethService.getTokenBalance(airdropAccountInfo.getAddress(), airdropAccountInfo.getContract());

        if (tokenBalance.compareTo(airdropNum) < 0) {
            return new FailResponse("空投账户token不足");
        }
        //所需gas费用
        BigDecimal fee = Convert.fromWei(getBigDecimal(airdropInfos, gasPrice, gasLimit), Convert.Unit.ETHER);
        logger.error("============空投需要的fee======" + fee);
        //链上eth余额
        BigDecimal balance = ethService.getBalance(airdropAccountInfo.getAddress());
        logger.error("============账户上余额======" + balance);
        if (balance.compareTo(fee) < 0) {
            return new FailResponse("空投账户eth不足");
        }
        //获取nonce
        BigInteger nonce = getNonce(airdropAccountInfo);
        //看log发送到第几笔了
        int i = 1;
        for (AirdropInfo airdropInfo : airdropInfos) {
            MessageResult messageResult = ethService.transferTokenFromAddress(airdropAccountInfo.getPrivateKey(), airdropAccountInfo.getContract(),
                    airdropInfo.getAddress(), gasPrice, Convert.toWei(airdropInfo.getAmount(), Convert.Unit.ETHER), gasLimit, nonce);
            if (messageResult.getCode() == 0) {
                logger.error("发送第" + i + "笔成功，地址为：" + airdropInfo.getAddress() + "交易tx:" + messageResult.getData().toString());
                AirdropTransactionOrder airdropTransactionOrder = new AirdropTransactionOrder();
                airdropTransactionOrder.setCoinName(airdropAccountInfo.getName());
                airdropTransactionOrder.setCoinNum(airdropInfo.getAmount());
                airdropTransactionOrder.setTxHash(messageResult.getData().toString());
                airdropTransactionOrder.setFromAddress(airdropAccountInfo.getAddress());
                airdropTransactionOrder.setGasPrice(new BigDecimal(gasPrice));
                airdropTransactionOrder.setGasLimit(gasLimit);
                airdropTransactionOrder.setNonce(nonce);
                airdropTransactionOrder.setToAddress(airdropInfo.getAddress());
                airdropTransactionOrder.setOrderStatus(Constants.AIRDROP_ORDER_PEEDING_STATUS);
                airdropTransactionOrder.setCreateTime(new Date());
                airdropTransactionOrder.setUpdateTime(new Date());
                airdropTransactionOrderMapper.insertSelective(airdropTransactionOrder);
                airdropInfoMapper.updateStatusById(airdropInfo.getId());
                nonce = nonce.add(new BigInteger("1"));
                i++;
            } else {
                logger.error("发送第" + i + "笔失败，地址为：" + airdropInfo.getAddress() +"===nonce为："+nonce+"===金额为"+airdropInfo.getAmount());
                nonce = nonce.add(new BigInteger("1"));
                i++;
            }
        }
        airdropAccountInfoDAO.updateAirdropAccount(nonce, airdropAccountInfo.getId());
        return new SuccessResponse("Successful");
    }

    private BigInteger getNonce(AirdropAccountInfo airdropAccountInfo) throws ExecutionException, InterruptedException {
        BigInteger accountNonce = ethService.getAccountNonce(airdropAccountInfo.getAddress());
        if (airdropAccountInfo.getNonce().compareTo(accountNonce) > 0) {
            accountNonce = airdropAccountInfo.getNonce();
        }
        return accountNonce;
    }

    private BigDecimal getBigDecimal(List<AirdropInfo> airdropInfos, BigInteger gasPrice, BigInteger gasLimit) {
        return new BigDecimal(gasPrice).multiply(new BigDecimal(gasLimit)).multiply(new BigDecimal(airdropInfos.size()));
    }

    /**
     * 空投账户
     *
     * @param id
     * @return
     */
    private AirdropAccountInfo getAirdropAccountInfo(Integer id) {
        AirdropAccountInfoExample airdropAccountInfoExample = new AirdropAccountInfoExample();
        airdropAccountInfoExample.or().andIdEqualTo(id);
        return airdropAccountInfoMapper.selectByExample(airdropAccountInfoExample).stream().findFirst().orElse(null);
    }

    /**
     * 要进行空投的信息
     *
     * @param coinName
     * @return
     */
    private List<AirdropInfo> getAirdropInfos(String coinName) {
        AirdropInfoExample infoExample = new AirdropInfoExample();
        infoExample.or().andCoinNameEqualTo(coinName.toUpperCase()).andStatusEqualTo(Constants.AIRDROP_UNSTART_STATUS);
        return airdropInfoMapper.selectByExample(infoExample);
    }

    /**
     * 空投列表
     *
     * @param date
     * @return
     * @throws ParseException
     */
    public List<AirdropVo> getOcpAirdrop(String date) throws ParseException {
        Date firstDate = DateUtils.parse(date, "yyyyMM");
        Date addDay = DateUtils.addMonth(firstDate, Constants.ONE_INTEGER);
        List<AirdropVo> resultList = new ArrayList<>();
        List<String> addresses = new ArrayList<>();
        List<AirdropVo> airdropVos = ocnAccountDAO.selectAddressAndValue(DateUtils.format(firstDate, DateUtils.yyyyMM), DateUtils.format(addDay, DateUtils.yyyyMM));
        List<String> emailAddress = emailWalletAddressDAO.selectlimitAddress(DateUtils.format(addDay, DateUtils.yyyyMM));
        addresses.addAll(emailAddress);
        List<String> memberAddress = memberDAO.selectlimitAddress(DateUtils.format(addDay, DateUtils.yyyyMM));
        for (String address : memberAddress) {
            if (!addresses.contains(address)) {
                addresses.add(address);
            }
        }
        if (airdropVos != null && airdropVos.size() > 0) {
            for (AirdropVo airdropVo : airdropVos) {
                if (!addresses.contains(airdropVo.getAddress())) continue;
                airdropVo.setValue(new BigDecimal(airdropVo.getValue()).divide(Constants.AIRDROP_DIVID, 0, BigDecimal.ROUND_DOWN).toString());
                resultList.add(airdropVo);

            }
        }
        return resultList;
    }

    /**
     * 重发
     */
    public void resend() {
        AirdropAccountInfo airdropAccountInfo = getAirdropAccountInfo(1);
        AirdropTransactionOrderExample airdropTransactionOrderExample = new AirdropTransactionOrderExample();
        airdropTransactionOrderExample.or().andOrderStatusEqualTo(Constants.AIRDROP_ORDER_PEEDING_STATUS);
        List<AirdropTransactionOrder> airdropTransactionOrders = airdropTransactionOrderMapper.selectByExample(airdropTransactionOrderExample);
        if (airdropTransactionOrders == null || airdropTransactionOrders.size() <= 0) {
            return;
        }
        for (AirdropTransactionOrder airdropTransactionOrder : airdropTransactionOrders) {
            BigInteger gasPrice = airdropTransactionOrder.getGasPrice().add(Convert.toWei(new BigDecimal("1"), Convert.Unit.GWEI)).toBigInteger();
            MessageResult messageResult = ethService.transferTokenFromAddress(airdropAccountInfo.getPrivateKey(), airdropAccountInfo.getContract(),
                    airdropTransactionOrder.getToAddress(), gasPrice, Convert.toWei(airdropTransactionOrder.getCoinNum(), Convert.Unit.ETHER), Constants.AIRDROP_GASLIMIT, airdropTransactionOrder.getNonce());
            if (messageResult.getCode() == 0) {
                AirdropTransactionOrder updateOrder = new AirdropTransactionOrder();
                updateOrder.setId(airdropTransactionOrder.getId());
                updateOrder.setTxHash(messageResult.getData().toString());
                updateOrder.setGasPrice(new BigDecimal(gasPrice));
                updateOrder.setUpdateTime(new Date());
                airdropTransactionOrderMapper.updateByPrimaryKeySelective(updateOrder);
            } else {
                logger.error("发送失败，地址为：" + airdropTransactionOrder.getToAddress() + "交易tx:" + messageResult.getData().toString());
            }

        }
    }
}
