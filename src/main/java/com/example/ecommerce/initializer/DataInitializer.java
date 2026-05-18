package com.example.ecommerce.initializer;

import com.example.ecommerce.entity.*;
import com.example.ecommerce.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GoodsRepository goodsRepository;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ClickLogRepository clickLogRepository;
    @Autowired
    private SystemLogRepository systemLogRepository;

    @Override
    public void run(String... args) throws Exception {
        // 初始化用户
        if (userRepository.count() == 0) {
            initUsers();
            System.out.println("初始化用户数据完成");
        }

        // 初始化商品（20+个）
        if (goodsRepository.count() == 0) {
            initGoods();
            System.out.println("初始化商品数据完成");
        }

        // 初始化订单
    }

    private void initUsers() {
        User user = new User();
        user.setUserName("zhangsan");
        user.setPassword("123456");
        user.setNickName("小明");
        user.setName("张三");
        user.setAge(28);
        user.setSex("男");
        user.setSchool("清华大学");
        user.setAddress("北京市海淀区清华园1号");
        user.setCodeNum("11010119900307663X");
        user.setPhone("13800001111");
        user.setCreatedAt(new Date());
        userRepository.save(user);

        User user2 = new User();
        user2.setUserName("lisi");
        user2.setPassword("123456");
        user2.setNickName("李四");
        user2.setName("李四");
        user2.setAge(32);
        user2.setSex("男");
        user2.setSchool("北京大学");
        user2.setAddress("北京市海淀区颐和园路5号");
        user2.setCodeNum("11010119900101123X");
        user2.setPhone("13900002222");
        user2.setCreatedAt(new Date());
        userRepository.save(user2);

        User user3 = new User();
        user3.setUserName("wangwu");
        user3.setPassword("123456");
        user3.setNickName("王五");
        user3.setName("王五");
        user3.setAge(25);
        user3.setSex("女");
        user3.setSchool("复旦大学");
        user3.setAddress("上海市杨浦区邯郸路220号");
        user3.setCodeNum("31010119950101444X");
        user3.setPhone("15000003333");
        user3.setCreatedAt(new Date());
        userRepository.save(user3);
    }

    private void initGoods() {
        // ========== 笔记本电脑类 (8个) ==========

        Goods goods1 = new Goods();
        goods1.setGoodsId(4380878);
        goods1.setGoodsInfo("华硕FX53VD 15.6英寸游戏本 i5-7300HQ 8G 1T GTX1050");
        goods1.setSpecificationsInfo("处理器：i5-7300HQ 显卡：GTX1050 内存：8G 硬盘：1T 屏幕：15.6英寸");
        goods1.setAdInfo("开学季特惠，游戏本首选");
        goods1.setPrice(529900);
        goods1.setStock(50);
        goods1.setCategory("笔记本电脑");
        goodsRepository.save(goods1);

        Goods goods2 = new Goods();
        goods2.setGoodsId(4380879);
        goods2.setGoodsInfo("联想小新Pro16 2024款 2.5K高刷屏 高性能轻薄本");
        goods2.setSpecificationsInfo("处理器：R7-8845H 显卡：集显 内存：32G 硬盘：1T SSD 屏幕：16英寸 2.5K");
        goods2.setAdInfo("AI超能本，轻薄便携性能强");
        goods2.setPrice(599900);
        goods2.setStock(80);
        goods2.setCategory("笔记本电脑");
        goodsRepository.save(goods2);

        Goods goods3 = new Goods();
        goods3.setGoodsId(4380880);
        goods3.setGoodsInfo("苹果MacBook Air 13.6英寸 M3芯片");
        goods3.setSpecificationsInfo("处理器：M3芯片 内存：16G 硬盘：512G SSD 屏幕：13.6英寸 Liquid视网膜");
        goods3.setAdInfo("M3芯片，续航长达18小时");
        goods3.setPrice(899900);
        goods3.setStock(30);
        goods3.setCategory("笔记本电脑");
        goodsRepository.save(goods3);

        Goods goods4 = new Goods();
        goods4.setGoodsId(4380881);
        goods4.setGoodsInfo("华为MateBook X Pro 微绒典藏版 3.1K原色屏");
        goods4.setSpecificationsInfo("处理器：酷睿Ultra7 内存：16G 硬盘：1T SSD 屏幕：14.2英寸 3.1K");
        goods4.setAdInfo("超级终端，跨设备协同");
        goods4.setPrice(999900);
        goods4.setStock(25);
        goods4.setCategory("笔记本电脑");
        goodsRepository.save(goods4);

        Goods goods5 = new Goods();
        goods5.setGoodsId(4380882);
        goods5.setGoodsInfo("戴尔游匣G16 16英寸游戏本 2.5K 240Hz");
        goods5.setSpecificationsInfo("处理器：i9-13900HX 显卡：RTX4060 内存：16G 硬盘：1T SSD");
        goods5.setAdInfo("外星人散热技术，游戏神器");
        goods5.setPrice(1199900);
        goods5.setStock(40);
        goods5.setCategory("笔记本电脑");
        goodsRepository.save(goods5);

        Goods goods6 = new Goods();
        goods6.setGoodsId(4380883);
        goods6.setGoodsInfo("小米Redmi Book Pro 15 2023锐龙版");
        goods6.setSpecificationsInfo("处理器：R7-7840HS 显卡：集显 内存：16G 硬盘：512G SSD");
        goods6.setAdInfo("高性价比办公本");
        goods6.setPrice(499900);
        goods6.setStock(100);
        goods6.setCategory("笔记本电脑");
        goodsRepository.save(goods6);

        Goods goods7 = new Goods();
        goods7.setGoodsId(4380884);
        goods7.setGoodsInfo("惠普战66六代 14英寸 商务办公本");
        goods7.setSpecificationsInfo("处理器：i5-1340P 内存：16G 硬盘：1T SSD 屏幕：14英寸");
        goods7.setAdInfo("军工品质，上门售后");
        goods7.setPrice(449900);
        goods7.setStock(60);
        goods7.setCategory("笔记本电脑");
        goodsRepository.save(goods7);

        Goods goods8 = new Goods();
        goods8.setGoodsId(4380885);
        goods8.setGoodsInfo("ThinkPad X1 Carbon Gen11 超轻薄商务本");
        goods8.setSpecificationsInfo("处理器：i7-1365U 内存：32G 硬盘：1T SSD 屏幕：14英寸 4K");
        goods8.setAdInfo("碳纤维机身，指纹解锁");
        goods8.setPrice(1499900);
        goods8.setStock(15);
        goods8.setCategory("笔记本电脑");
        goodsRepository.save(goods8);

        // ========== 手机类 (7个) ==========

        Goods goods9 = new Goods();
        goods9.setGoodsId(4380901);
        goods9.setGoodsInfo("苹果iPhone 15 Pro Max 256GB 原色钛金属");
        goods9.setSpecificationsInfo("屏幕：6.7英寸 芯片：A17 Pro 相机：4800万主摄 电池：大容量");
        goods9.setAdInfo("钛金属设计，A17 Pro芯片");
        goods9.setPrice(999900);
        goods9.setStock(120);
        goods9.setCategory("手机");
        goodsRepository.save(goods9);

        Goods goods10 = new Goods();
        goods10.setGoodsId(4380902);
        goods10.setGoodsInfo("华为Mate 60 Pro 12GB+512GB 雅丹黑");
        goods10.setSpecificationsInfo("屏幕：6.82英寸 芯片：麒麟9000s 相机：XMAGE影像 电池：5000mAh");
        goods10.setAdInfo("卫星通话，超可靠玄武架构");
        goods10.setPrice(699900);
        goods10.setStock(80);
        goods10.setCategory("手机");
        goodsRepository.save(goods10);

        Goods goods11 = new Goods();
        goods11.setGoodsId(4380903);
        goods11.setGoodsInfo("小米14 Pro 16GB+1TB 岩石青");
        goods11.setSpecificationsInfo("屏幕：6.73英寸 芯片：骁龙8Gen3 相机：徕卡三摄 电池：4880mAh");
        goods11.setAdInfo("徕卡光学，澎湃OS");
        goods11.setPrice(499900);
        goods11.setStock(150);
        goods11.setCategory("手机");
        goodsRepository.save(goods11);

        Goods goods12 = new Goods();
        goods12.setGoodsId(4380904);
        goods12.setGoodsInfo("三星Galaxy S24 Ultra 12GB+256GB 钛灰");
        goods12.setSpecificationsInfo("屏幕：6.8英寸 芯片：骁龙8Gen3 相机：2亿像素 电池：5000mAh");
        goods12.setAdInfo("AI智享生活，S Pen书写");
        goods12.setPrice(969900);
        goods12.setStock(60);
        goods12.setCategory("手机");
        goodsRepository.save(goods12);

        Goods goods13 = new Goods();
        goods13.setGoodsId(4380905);
        goods13.setGoodsInfo("荣耀Magic6 Pro 16GB+512GB 绒黑色");
        goods13.setSpecificationsInfo("屏幕：6.8英寸 芯片：骁龙8Gen3 相机：鹰眼相机 电池：5600mAh");
        goods13.setAdInfo("巨犀玻璃，鸿燕卫星通信");
        goods13.setPrice(569900);
        goods13.setStock(90);
        goods13.setCategory("手机");
        goodsRepository.save(goods13);

        Goods goods14 = new Goods();
        goods14.setGoodsId(4380906);
        goods14.setGoodsInfo("一加12 16GB+512GB 留白");
        goods14.setSpecificationsInfo("屏幕：2K东方屏 芯片：骁龙8Gen3 相机：哈苏影像 电池：5400mAh");
        goods14.setAdInfo("哈苏全焦段影像，东方屏");
        goods14.setPrice(479900);
        goods14.setStock(70);
        goods14.setCategory("手机");
        goodsRepository.save(goods14);

        Goods goods15 = new Goods();
        goods15.setGoodsId(4380907);
        goods15.setGoodsInfo("vivo X100 Pro 16GB+512GB 星迹蓝");
        goods15.setSpecificationsInfo("屏幕：6.78英寸 芯片：天玑9300 相机：蔡司影像 电池：5400mAh");
        goods15.setAdInfo("蔡司APO超级长焦，自研芯片V3");
        goods15.setPrice(499900);
        goods15.setStock(65);
        goods15.setCategory("手机");
        goodsRepository.save(goods15);

        // ========== 配件类 (7个) ==========

        Goods goods16 = new Goods();
        goods16.setGoodsId(4381001);
        goods16.setGoodsInfo("苹果AirPods Pro 第二代 主动降噪耳机");
        goods16.setSpecificationsInfo("Type-C接口，H2芯片，自适应音频，最长30小时续航");
        goods16.setAdInfo("降噪效果提升2倍");
        goods16.setPrice(189900);
        goods16.setStock(200);
        goods16.setCategory("配件");
        goodsRepository.save(goods16);

        Goods goods17 = new Goods();
        goods17.setGoodsId(4381002);
        goods17.setGoodsInfo("小米手环9 Pro 智能手环");
        goods17.setSpecificationsInfo("1.74英寸AMOLED屏，GPS定位，14天续航，心率血氧监测");
        goods17.setAdInfo("专业运动健康助手");
        goods17.setPrice(39900);
        goods17.setStock(300);
        goods17.setCategory("配件");
        goodsRepository.save(goods17);

        Goods goods18 = new Goods();
        goods18.setGoodsId(4381003);
        goods18.setGoodsInfo("罗技MX Master 3S 无线鼠标");
        goods18.setSpecificationsInfo("8000DPI，MagSpeed滚轮，静音按键，3设备切换");
        goods18.setAdInfo("旗舰办公鼠标");
        goods18.setPrice(59900);
        goods18.setStock(120);
        goods18.setCategory("配件");
        goodsRepository.save(goods18);

        Goods goods19 = new Goods();
        goods19.setGoodsId(4381004);
        goods19.setGoodsInfo("华为FreeBuds Pro 3 真无线降噪耳机");
        goods19.setSpecificationsInfo("麒麟A2芯片，智慧动态降噪3.0，星闪连接，33小时续航");
        goods19.setAdInfo("业界领先降噪深度");
        goods19.setPrice(149900);
        goods19.setStock(180);
        goods19.setCategory("配件");
        goodsRepository.save(goods19);

        Goods goods20 = new Goods();
        goods20.setGoodsId(4381005);
        goods20.setGoodsInfo("倍思65W氮化镓快充充电头");
        goods20.setSpecificationsInfo("2C1A接口，支持多种快充协议，可充笔记本/手机/平板");
        goods20.setAdInfo("小体积大能量");
        goods20.setPrice(12900);
        goods20.setStock(500);
        goods20.setCategory("配件");
        goodsRepository.save(goods20);

        Goods goods21 = new Goods();
        goods21.setGoodsId(4381006);
        goods21.setGoodsInfo("雷蛇黑寡妇蜘蛛V4 机械键盘");
        goods21.setSpecificationsInfo("绿轴，RGB幻彩，多功能旋钮，掌托设计");
        goods21.setAdInfo("游戏电竞键盘");
        goods21.setPrice(89900);
        goods21.setStock(45);
        goods21.setCategory("配件");
        goodsRepository.save(goods21);

        Goods goods22 = new Goods();
        goods22.setGoodsId(4381007);
        goods22.setGoodsInfo("绿联Type-C扩展坞 九合一");
        goods22.setSpecificationsInfo("HDMI 4K，PD100W，USB3.0*3，千兆网口，SD/TF卡槽");
        goods22.setAdInfo("笔记本扩展首选");
        goods22.setPrice(19900);
        goods22.setStock(150);
        goods22.setCategory("配件");
        goodsRepository.save(goods22);
    }
}