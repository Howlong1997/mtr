package com.gowell.mes.mtr.service;

import com.gowell.mes.mtr.Constants;
import com.gowell.mes.mtr.model.DeviceEntity;
import com.gowell.mes.mtr.repository.DeviceJpaRepository;
import com.gowell.mes.mtr.utils.StatusManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * @date: 2020/4/3
 * @time: 11:34
 */
@Service
public class IntegrateService {

    @Autowired
    DeviceJpaRepository deviceRepository;

    @Value("${curtain.delay}")
    private Integer delay;

    @Resource
    SocketApiService socketApiService;

    private static Boolean flag = true;

    /**
     * 幕布
     *
     * @param effectId 1:上升；2:下降
     * @return
     * @throws AWTException
     */
    public Integer asynEffectCurtain(String effectId) throws AWTException {
        List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_CURTAIN);
        if (CollectionUtils.isEmpty(devices)) {
            return -1;//设备未找到
        }
        if (!flag) {
            return -2; //设备正在运作，请稍后再试！
        }

        DeviceEntity device = devices.get(0);

        if (device.getOnoff() == Integer.valueOf(effectId)) {
            return -3;//设备已为该状态
        }
        operationCurtain(effectId, device);
        return 0;//操作成功！
    }

    /**
     * 幕布执行方法
     *
     * @param effectId 1:上升；2:下降
     * @param device   幕布设备
     * @throws AWTException
     */
    public synchronized void operationCurtain(String effectId, DeviceEntity device) throws AWTException {
        Timer timer = new Timer();
        flag = false;
        device.setProperty8("" + System.currentTimeMillis());
        device.setStatus(Constants.COMMAND_SETUP);
        device.setCmdstring(effectId);
        device.setTries(3);
        deviceRepository.save(device);
        new StatusManager().getStatus(deviceRepository);
        timer.schedule(new TimerTask() {
            public void run() {
                flag = true;
                this.cancel();
            }
        }, delay);
    }

    /**
     * 灯光 asynPowerOn
     *
     * @param lampId 灯光ID
     * @return
     */
    public Integer asynPowerOnLamp(String lampId) {
        List<DeviceEntity> devices = deviceRepository.findAllByNameAndCategory(lampId, Constants.DEVICE_LAMP);
        if (CollectionUtils.isEmpty(devices)) {
            return -1;
        }
        DeviceEntity device = devices.get(0);
        device.setStatus(Constants.COMMAND_SETUP);
        device.setCmdstring("" + Constants.POWER_ON);
        device.setTries(3);
        deviceRepository.save(device);
        return 0;
    }

    /**
     * 灯光asynPowerOff
     *
     * @param lampId 灯光ID
     * @return
     */
    public Integer asynPowerOffLamp(String lampId) {
        List<DeviceEntity> devices = deviceRepository.findAllByNameAndCategory(lampId, Constants.DEVICE_LAMP);
        if (CollectionUtils.isEmpty(devices)) {
            return -1;
        }
        DeviceEntity device = devices.get(0);
        device.setStatus(Constants.COMMAND_SETUP);
        device.setCmdstring("" + Constants.POWER_OFF);
        device.setTries(3);
        deviceRepository.save(device);
        return 0;
    }

    /**
     * 灯光组asynGroupOn
     *
     * @param groupId 灯组ID
     * @return
     */
    public Integer asynGroupOnLamp(String groupId) {
        List<DeviceEntity> devices = deviceRepository.findAllByCategoryOrderByIdAsc(Constants.DEVICE_LAMP);
        if (CollectionUtils.isEmpty(devices)) {
            return -1;
        }
        for (DeviceEntity device : devices) {
            if (groupId.equals(device.getProperty2())) {
                device.setOnoff(Constants.POWER_ON);
                device.setProperty8(null);
                device.setProperty9("" + System.currentTimeMillis());
                device.setStatus(null);
                device.setCmdstring(null);
                device.setTries(0);
                deviceRepository.save(device);
            }
        }
        String cmdString = groupId.equals("1") ? "M110FFFF" : "M210FFFF";
        if (!socketApiService.execute(cmdString))
            return -5;//执行命令失败
        return 0;
    }

    /**
     * 灯光组asynGroupOnOff
     *
     * @param groupId 灯组ID
     * @return
     */
    public Integer asynGroupOnOffLamp(@RequestParam(required = true) String groupId) {
        List<DeviceEntity> devices = deviceRepository.findAllByCategoryOrderByIdAsc(Constants.DEVICE_LAMP);
        if (CollectionUtils.isEmpty(devices)) {
            return -1;
        }
        for (DeviceEntity device : devices) {
            if (groupId.equals(device.getProperty2())) {
                device.setOnoff(Constants.POWER_OFF);
                device.setProperty8(null);
                device.setProperty9("" + System.currentTimeMillis());
                device.setStatus(null);
                device.setCmdstring(null);
                device.setTries(0);
                deviceRepository.save(device);
            }
        }
        String cmdString = groupId.equals("1") ? "M100FFFF" : "M200FFFF";
        if (!socketApiService.execute(cmdString))
            return -5;
        return 0;
    }

    /**
     * 玻璃asynPowerOnGlass
     *
     * @param glassId 玻璃ID
     * @return
     */
    public Integer asynPowerOnGlass(String glassId) {
        DeviceEntity device = null;
        List<DeviceEntity> devices = deviceRepository.findAllByNameAndCategory(Constants.DEVICE_GLASS_SPECIAL,
                Constants.DEVICE_GLASS);
        if (!CollectionUtils.isEmpty(devices)) {
            device = devices.get(0);
            if (!StringUtils.isEmpty(device.getCmdstring())) {
                return -4;//设备令异常-NULL
            }
            String mode = device.getProperty1();
            if (device.getOnoff() == Constants.POWER_ON && !StringUtils.isEmpty(mode) && !"0".equals(mode)) {
                return -6;//.........设备正在运行（?可能）
            }
        }
        devices = deviceRepository.findAllByNameAndCategory(glassId, Constants.DEVICE_GLASS);
        if (CollectionUtils.isEmpty(devices)) {
            return -1;
        }
        device = devices.get(0);
        device.setStatus(Constants.COMMAND_SETUP);
        device.setCmdstring("" + Constants.POWER_ON);
        device.setTries(3);
        deviceRepository.save(device);
        return 0;
    }

    /**
     * asynPowerOffGlass
     * glassId 玻璃ID
     *
     * @param glassId 玻璃ID
     * @return
     */
    public Integer asynPowerOffGlass(String glassId) {
        DeviceEntity device = null;
        List<DeviceEntity> devices = deviceRepository.findAllByNameAndCategory(Constants.DEVICE_GLASS_SPECIAL,
                Constants.DEVICE_GLASS);
        if (!CollectionUtils.isEmpty(devices)) {
            device = devices.get(0);
            if (!StringUtils.isEmpty(device.getCmdstring())) {
                return -4;
            }
            String mode = device.getProperty1();
            if (device.getOnoff() == Constants.POWER_ON && !StringUtils.isEmpty(mode) && !"0".equals(mode)) {
                return -6;
            }
        }
        devices = deviceRepository.findAllByNameAndCategory(glassId, Constants.DEVICE_GLASS);
        if (CollectionUtils.isEmpty(devices)) {
            return -1;
        }
        device = devices.get(0);
        device.setStatus(Constants.COMMAND_SETUP);
        device.setCmdstring("" + Constants.POWER_OFF);
        device.setTries(3);
        deviceRepository.save(device);
        return 0;
    }

    /**
     * 玻璃asynEffectOff
     *
     * @return
     */
    public Integer asynEffectOffGlass() {
        DeviceEntity device = null;
        List<DeviceEntity> devices = deviceRepository.findAllByNameAndCategory(Constants.DEVICE_GLASS_SPECIAL,
                Constants.DEVICE_GLASS);
        if (CollectionUtils.isEmpty(devices)) {
            return -1;
        }
        device = devices.get(0);
        device.setStatus(Constants.COMMAND_SETUP);
        device.setCmdstring("T0");
        device.setTries(3);
        deviceRepository.save(device);
        return 0;
    }

    /**
     * 玻璃asynEffectOn
     *
     * @param effectId 玻璃ID
     * @return
     */
    public Integer asynEffectOnGlass(String effectId) {
        List<DeviceEntity> devices = deviceRepository.findAllByNameAndCategory(Constants.DEVICE_GLASS_SPECIAL,
                Constants.DEVICE_GLASS);
        if (CollectionUtils.isEmpty(devices)) {
            return -1;
        }
        devices = deviceRepository.findAllByCategoryOrderByIdAsc(Constants.DEVICE_GLASS);
        for (DeviceEntity device : devices) {
            if (device.getName().equals(Constants.DEVICE_GLASS_SPECIAL)) {
                device.setStatus(Constants.COMMAND_SETUP);
                device.setCmdstring("T" + effectId);
                device.setTries(3);
                deviceRepository.save(device);
            } else {
                device.setOnoff(Constants.POWER_OFF);
                device.setStatus(null);
                device.setCmdstring(null);
                device.setTries(0);
                deviceRepository.save(device);
            }
        }
        return 0;
    }

    /**
     * 玻璃组asynGroupOnGlass
     *
     * @param groupId 玻璃组ID
     * @return
     */
    public Integer asynGroupOnGlass(String groupId) {
        List<DeviceEntity> devices = deviceRepository.findAllByNameAndCategory(Constants.DEVICE_GLASS_SPECIAL,
                Constants.DEVICE_GLASS);
        if (!CollectionUtils.isEmpty(devices)) {
            DeviceEntity device = devices.get(0);
            if (!StringUtils.isEmpty(device.getCmdstring())) {
                return -4;
            }
            String mode = device.getProperty1();
            if (device.getOnoff() == Constants.POWER_ON && !StringUtils.isEmpty(mode) && !"0".equals(mode)) {
                return -6;
            }
        }
        devices = deviceRepository.findAllByCategoryOrderByIdAsc(Constants.DEVICE_GLASS);
        if (CollectionUtils.isEmpty(devices)) {
            return -1;
        }

        for (DeviceEntity device : devices) {
            if (groupId.equals(device.getProperty2())) {
                device.setStatus(Constants.COMMAND_SETUP);
                device.setCmdstring("" + Constants.POWER_ON);
                device.setTries(3);
                deviceRepository.save(device);
            }
        }
        return 0;
    }

    /**
     * 玻璃asynGroupOffGlass
     *
     * @param groupId 玻璃组ID
     * @return
     */
    public Integer asynGroupOffGlass(String groupId) {
        List<DeviceEntity> devices = deviceRepository.findAllByNameAndCategory(Constants.DEVICE_GLASS_SPECIAL,
                Constants.DEVICE_GLASS);
        if (!CollectionUtils.isEmpty(devices)) {
            DeviceEntity device = devices.get(0);
            if (!StringUtils.isEmpty(device.getCmdstring())) {
                return -4;
            }
            String mode = device.getProperty1();
            if (device.getOnoff() == Constants.POWER_ON && !StringUtils.isEmpty(mode) && !"0".equals(mode)) {
                return -6;
            }
        }
        devices = deviceRepository.findAllByCategoryOrderByIdAsc(Constants.DEVICE_GLASS);
        if (CollectionUtils.isEmpty(devices)) {
            return -1;
        }
        for (DeviceEntity device : devices) {
            if (groupId.equals(device.getProperty2())) {
                device.setStatus(Constants.COMMAND_SETUP);
                device.setCmdstring("" + Constants.POWER_OFF);
                device.setTries(3);
                deviceRepository.save(device);
            }
        }
        return 0;
    }

    /**
     * 大屏asynPowerOnProjector
     *
     * @return
     */
    public Integer asynPowerOnMonitor() {
        List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_MONITOR);
        if (CollectionUtils.isEmpty(devices)) {
            return -1;
        }
        DeviceEntity device = devices.get(0);
        if (!StringUtils.isEmpty(device.getCmdstring())) {
            return -4;
        }
        if (device.getOnoff() != Constants.POWER_ON) {
            device.setStatus(Constants.COMMAND_SETUP);
            device.setCmdstring("" + Constants.POWER_ON);
            device.setTries(3);
            deviceRepository.save(device);
        }
        return 0;
    }

    /**
     * 大屏asynPowerOff
     *
     * @return
     */
    public Integer asynPowerOffMonitor() {
        List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_MONITOR);
        if (CollectionUtils.isEmpty(devices)) {
            return -1;
        }
        DeviceEntity device = devices.get(0);
        if (!StringUtils.isEmpty(device.getCmdstring())) {
            return -4;
        }
        if (device.getOnoff() != Constants.POWER_OFF) {
            device.setStatus(Constants.COMMAND_SETUP);
            device.setCmdstring("" + Constants.POWER_OFF);
            device.setTries(3);
            deviceRepository.save(device);
        }
        return 0;
    }


    /**
     * 投影仪asynPowerOn
     *
     * @return
     */
    public Integer asynPowerOnProjector() {
        List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_PROJECTOR);
        if (CollectionUtils.isEmpty(devices)) {
            return -1;
        }
        DeviceEntity device = devices.get(0);
        if (!StringUtils.isEmpty(device.getCmdstring())) {
            return -4;
        }
        if (device.getOnoff() != Constants.POWER_ON) {
            device.setStatus(Constants.COMMAND_SETUP);
            device.setCmdstring("" + Constants.POWER_ON);
            device.setTries(3);
            deviceRepository.save(device);
        }
        return 0;
    }

    /**
     * 投影仪asynPowerOffProjector
     *
     * @return
     */
    public Integer asynPowerOffProjector() {
        List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_PROJECTOR);
        if (CollectionUtils.isEmpty(devices)) {
            return -1;
        }
        DeviceEntity device = devices.get(0);
        if (!StringUtils.isEmpty(device.getCmdstring())) {
            return -4;
        }
        if (device.getOnoff() != Constants.POWER_OFF) {
            device.setStatus(Constants.COMMAND_SETUP);
            device.setCmdstring("" + Constants.POWER_OFF);
            device.setTries(3);
            deviceRepository.save(device);
        }
        return 0;
    }

    /**
     * 投影仪asynBrightup
     *
     * @param brightness 调亮参数
     * @return
     */
    public Integer asynBrightup(int brightness) {
        List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_PROJECTOR);
        if (CollectionUtils.isEmpty(devices)) {
            return -1;
        }
        DeviceEntity device = devices.get(0);
        if (!StringUtils.isEmpty(device.getCmdstring())) {
            return -2;
        }
        if (device.getOnoff() != Constants.POWER_ON) {
            return -7;
        }
        device.setStatus(Constants.COMMAND_SETUP);
        device.setCmdstring("B" + brightness);
        device.setTries(3);
        deviceRepository.save(device);
        return 0;
    }

    /**
     * 大屏asynVolume
     *
     * @param volume 亮度参数
     * @return
     */
    public Integer asynVolume(@RequestParam int volume) {
        List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_PROJECTOR);
        if (CollectionUtils.isEmpty(devices)) {
            return -1;
        }
        DeviceEntity device = devices.get(0);
        if (!StringUtils.isEmpty(device.getCmdstring())) {
            return -4;
        }
        if (device.getOnoff() != Constants.POWER_ON) {
            return -7;
        }
        device.setStatus(Constants.COMMAND_SETUP);
        device.setCmdstring("V" + volume);
        device.setTries(3);
        deviceRepository.save(device);
        return 0;
    }

    /**
     * 投影仪asynMuteOn
     *
     * @return
     */
    public Integer asynMuteOnProjector() {
        List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_PROJECTOR);
        if (CollectionUtils.isEmpty(devices)) {
            return -1;
        }
        DeviceEntity device = devices.get(0);
        if (!StringUtils.isEmpty(device.getCmdstring())) {
            return -4;
        }
        if (device.getOnoff() != Constants.POWER_ON) {
            return -7;
        }
        device.setStatus(Constants.COMMAND_SETUP);
        device.setCmdstring("M1");
        device.setTries(3);
        deviceRepository.save(device);
        return 0;
    }

    /**
     * 投影仪asynMuteOff
     *
     * @return
     */
    public Integer asynMuteOffProjector() {
        List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_PROJECTOR);
        if (CollectionUtils.isEmpty(devices)) {
            return -1;
        }
        DeviceEntity device = devices.get(0);
        if (!StringUtils.isEmpty(device.getCmdstring())) {
            return -4;
        }
        if (device.getOnoff() != Constants.POWER_ON) {
            return -7;
        }
        device.setStatus(Constants.COMMAND_SETUP);
        device.setCmdstring("M0");
        device.setTries(3);
        deviceRepository.save(device);
        return 0;
    }

    /**
     * 投影仪asynStatus
     *
     * @return
     */
    public Map<String, Object> asynStatusProjector() {
        Map<String, Object> map = new HashMap<String, Object>();
        List<DeviceEntity> devices = deviceRepository.findAllByCategory(Constants.DEVICE_PROJECTOR);
        if (CollectionUtils.isEmpty(devices)) {
            map.put("errorCode", -1);
            return map;
        }
        DeviceEntity device = devices.get(0);
        HashMap<String, String> data = new HashMap<String, String>();
        data.put("onoff", "" + device.getOnoff());
        if (device.getOnoff() == Constants.POWER_ON) {
            data.put("brightness", device.getProperty1());
            data.put("volumn", device.getProperty2());
            data.put("mute", device.getProperty3());
        }
        map.put("errorCode", 0);
        map.put("data", data);
        return map;
    }

    /**
     * 一键总开关
     *
     * @param power 0：关；1：开
     * @return
     * @throws AWTException
     */
    public String masterSwitch(Integer power) throws AWTException {
        StringBuffer sbf = new StringBuffer();
        List<DeviceEntity> devices = deviceRepository.findAll();
        String curtain = "2";
        if (power == 0) {
            curtain = "1";
        }
        if (CollectionUtils.isEmpty(devices)) {
            sbf.append("设备未找到");
        }
        for (DeviceEntity device : devices) {
            switch (device.getName()) {
                case "":
                    break;
                default:
                    //大屏S1、投影仪P1、玻璃（大）G1、（小）G6、灯光L1、L2、L3、L4
                    if ((device.getName().contains("S1") || device.getName().contains("P1") || device.getName().contains("G")) && !StringUtils.isEmpty(device.getCmdstring())) {
                        if (device.getName().contains("G") && (device.getOnoff() == power && !StringUtils.isEmpty(device.getProperty1()) && !"0".equals(device.getProperty1()))) {
                            sbf.append("玻璃：");
                            sbf.append(device.getName());
                            sbf.append("--工作状态异常");
                            break;
                        }
                        sbf.append("设备：");
                        sbf.append(device.getName());
                        sbf.append("--指令异常.");
                        break;
                    }
                    device.setStatus(Constants.COMMAND_SETUP);
                    device.setCmdstring(power.toString());
                    device.setTries(3);
                    break;
            }
            //幕布C1
            while (device.getName().contains("C1")) {
                if (!flag) {
                    sbf.append("幕布：C1--设备正在运作，请稍后再试");
                }
                if (device.getOnoff() == Integer.valueOf(curtain)) {
                    sbf.append("幕布：C1--设备已为该状态");
                    break;
                }
                operationCurtain(curtain, device);
                break;
            }

        }
        deviceRepository.saveAll(devices);
        return sbf.toString();
    }


    /**
     * 灯光总开关
     * Bug:
     * @param lampSwitch 0：关；1：开
     * @param lampNames  灯名称：L1...
     * @return
     */
    public String mainLampSwitch(Integer lampSwitch, String lampNames) {
        StringBuffer sbf = new StringBuffer();
        List<DeviceEntity> devices = deviceRepository.findAll();
        if (CollectionUtils.isEmpty(devices)) {
            sbf.append("设备未找到");
        }
        String[] lampArray = lampNames.split(",");
        for (DeviceEntity device : devices) {
            if (lampArray.length == 0) {
                while (device.getName().contains("L")) {
                    device.setStatus(Constants.COMMAND_SETUP);
                    device.setCmdstring(lampSwitch.toString());
                    device.setTries(3);
                    break;
                }
            } else {
                for (String lamp : lampArray) {
                    if (device.getName().contains(lamp)) {
                        device.setStatus(Constants.COMMAND_SETUP);
                        device.setCmdstring(lampSwitch.toString());
                        device.setTries(3);
                        break;
                    }
                }

            }
        }
        deviceRepository.saveAll(devices);
        return sbf.toString();
    }

    /**
     * 大会议室总开关
     * Bug:
     * @param largeMeetPower 0：关；1：开
     * @return
     */
    public String largeMeetSwitch(Integer largeMeetPower) {
        StringBuffer sbf = new StringBuffer();
        List<DeviceEntity> devices = deviceRepository.findAll();
        String[] largeMeets = Constants.MEETING_LARGE.split(",");
        if (CollectionUtils.isEmpty(devices)) {
            sbf.append("设备未找到");
        }
        for (DeviceEntity device : devices) {
            for (String _largeMeet : largeMeets) {
                if (device.getName().equals(_largeMeet)) {
                    device.setStatus(Constants.COMMAND_SETUP);
                    device.setCmdstring(largeMeetPower.toString());
                    device.setTries(3);
                    break;
                }
            }
        }
        deviceRepository.saveAll(devices);
        return null;
    }

    /**
     * 小会议室总开关
     * Bug:smallMeetPower：1--第一次500；幕布无法开启
     * @param smallMeetPower 0：关；1：开
     * @return
     */
    public String smallMeetSwitch(Integer smallMeetPower) throws AWTException {
        StringBuffer sbf = new StringBuffer();
        List<DeviceEntity> devices = deviceRepository.findAll();
        String curtain = "2";
        if (smallMeetPower == 0) {
            curtain = "1";
        }
        String[] largeMeets = Constants.MEETING_SMALL.split(",");
        if (CollectionUtils.isEmpty(devices)) {
            sbf.append("设备未找到");
        }
        for (DeviceEntity device : devices) {
            for (String _smallMeet : largeMeets) {
                if (device.getName().equals(_smallMeet)) {
                    if (device.getName().equals("C1")) {
                        if (!flag) {
                            sbf.append("幕布：C1--设备正在运作");
                            break;
                        }
                        if (device.getOnoff() == Integer.valueOf(curtain)) {
                            sbf.append("幕布：C1--设备已为改状态");
                            break;
                        }
                        operationCurtain(curtain, device);
                        devices.remove(device);
                        break;
                    }
                    device.setStatus(Constants.COMMAND_SETUP);
                    device.setCmdstring(smallMeetPower.toString());
                    device.setTries(3);
                    break;
                }
            }
        }
        deviceRepository.saveAll(devices);
        return null;
    }

    /**
     * 测试main
     *
     * @param args
     */
    public static void main(String[] args) {
        String a = "1";
        String[] as = a.split(",");
        for (String _a : as)
            System.out.println(_a.toString());
    }
}

