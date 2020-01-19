package shared

import scala.collection.mutable.ArrayBuffer
import scala.math.BigDecimal.RoundingMode

/**
  * Created by yz on 2019/3/6
  */
object Shared {

  val pathFinderPlus = ArrayBuffer("DYS19", "DYS385a/b", "DYS389I", "DYS389II", "DYS390", "DYS391", "DYS392", "DYS393",
    "DYS437", "DYS438", "DYS439", "DYS448", "DYS456", "DYS458", "DYS635", "YGATAH4", "DYS481", "DYS533",
    "DYS576", "DYS643", "DYS460", "DYS549", "DYF387S1", "DYS449", "DYS518", "DYS627", "DYS570", "DYS527a/b",
    "DYS447", "DYS444", "DYS557", "DYS596", "DYS522", "DYS593", "DYS388", "DYS645", "DYS404S1a/b", "rs771783753",
    "rs759551978", "rs199815934")

  val quickLines = ArrayBuffer("DYS19", "DYS385a/b", "DYS389I", "DYS389II", "DYS390", "DYS391", "DYS392", "DYS393", "DYS437",
    "DYS438", "DYS439", "DYS448", "DYS456", "DYS458", "DYS635", "YGATAH4", "DYS481", "DYS533", "DYS576",
    "DYS643", "DYS460", "DYS549", "DYF387S1", "DYS449", "DYS518", "DYS627", "DYS570", "DYS596", "DYS593",
    "DYS388", "DYS645", "DYS626")

  val strWeightSiteNames = (Shared.quickLines ++ Shared.pathFinderPlus).flatMap { x =>
    x match {
      case "DYS385a/b" => Array("DYS385a", "DYS385b")
      case "DYF387S1" => Array("DYS387a", "DYS387b")
      case "DYS527a/b" => Array("DYS527a", "DYS527b")
      case "DYS404S1a/b" => Array("DYS404S1a", "DYS404S1b")
      case _ => Array(x)
    }
  }.distinct

  val strMap = Map("QuickLine" -> quickLines, "PathFinder Plus" -> pathFinderPlus)

  val snpCoreset = ArrayBuffer("M217", "B473", "P47", "M201", "M407", "BY728", "M15", "F948", "Z1300", "M20", "M304", "M55", "F845",
    "M96", "M130", "F1396", "M174", "M77", "M48", "M170", "F1918", "F1756", "M9", "M89", "M231", "F14",
    "L901", "F1067", "P295", "M184")
  val snpN = ArrayBuffer("F3361", "M128", "F1833", "L1420", "M1854", "M46", "SK1512", "PH68", "CTS1714", "F4065", "F4250",
    "SK1507", "B187", "M231", "F4156", "F846", "F2584", "B197", "F839", "M1982", "SK2246", "F2930", "Z34965",
    "M2120", "B182", "PH432", "F4205", "M178", "CTS4714", "CTS1303")
  val snpP = ArrayBuffer("F1857", "F4529", "F4530", "F4531", "PH1003", "F1827", "M120", "M3", "F835", "L53", "F844", "M242",
    "YP771", "M420", "Y13199", "L54", "L330", "M207", "M25", "SK1927", "YP1102", "L275", "L232", "SK1925",
    "L278", "M269", "Y558", "M346", "M512", "F903")
  val snp01 = ArrayBuffer("M119", "F157", "CTS701", "F619", "K644", "F153", "Z23482", "SK1555", "SK1527", "M101", "P203.1",
    "F533", "F18460", "CTS5576", "F656", "CTS409", "SK1533", "SK1567", "M50", "CTS52", "F6226", "F65",
    "F4253", "F794", "F5498", "F446", "F3288", "Z23271", "F78", "Z38607")
  val snp02 = ArrayBuffer("M268", "F923", "F2868", "F1252", "M88", "Z23743", "PK4", "F2061", "SK1630", "F840", "F5503",
    "F1204", "F2758", "Y14005", "L682", "M1283", "M1368", "F1462", "M1410", "SK1636", "F809", "F2924",
    "F838", "F2489", "CTS713", "F2124", "F3079", "F993", "M95", "M176")
  val snpGamma = ArrayBuffer("F930", "G6795544A", "SK1691", "SK1676", "F197", "F196", "F2680", "F1422", "M122", "F2527",
    "F11", "F309", "F17", "F793", "F133", "F718", "F1495", "F856", "F377", "M324", "F18", "F38", "F632",
    "F3232", "SK1675", "F449", "P201", "M188", "N6", "CTS10573")
  val snpAlpha = ArrayBuffer("F141", "F316", "F14441", "B456", "F1442", "F375", "F438", "CTS7634", "CTS5063", "CTS4658",
    "G7690556T", "F14196", "F14245", "F14523", "A9462", "F2137", "F402", "F14274", "F1123", "F148",
    "M1543", "F8", "F317", "F813", "CTS9713", "F14682", "F7479", "F1754", "CTS1154", "F5970")
  val snpBeta = ArrayBuffer("F444", "F79", "F46", "F48", "F55", "F152", "F563", "F209", "F2173", "F14521", "CTS3776",
    "CTS3763", "KM3028", "F5535", "F4249", "F3607", "F823", "CTS335", "L1360", "KM3031", "CTS53",
    "F1326", "F2903", "CTS4266", "CTS1933", "CTS1346", "F14214", "CTS2056")

  val snpMap = Map(
    "coreset" -> snpCoreset, "N" -> snpN, "P" -> snpP, "O1a" -> snp01, "O1b" -> snp02,
    "Oγ" -> snpGamma, "Oα" -> snpAlpha, "Oβ" -> snpBeta
  )

  val snpKitNames = ArrayBuffer("coreset", "N", "P", "O1a", "O1b", "Oγ", "Oα", "Oβ")

  val panels = ArrayBuffer("N", "P", "O1a", "O1b", "Oγ", "Oα", "Oβ")

  case class SiteNameData(siteNames: Set[String], otherNameMap: Map[String, String], kitName: String)

  val strSiteNameDatas = ArrayBuffer(
    SiteNameData(Shared.quickLines.toSet, Map("DYS385" -> "DYS385a/b", "GATA_H4" -> "YGATAH4", "H4" -> "YGATAH4", "DYS387a/b" -> "DYF387S1"
    ), "QuickLine"),
    SiteNameData(Shared.pathFinderPlus.toSet, Map("DYS385" -> "DYS385a/b", "GATA_H4" -> "YGATAH4", "H4" -> "YGATAH4", "DYS387a/b" -> "DYF387S1",
      "DYS527" -> "DYS527a/b", "DYF404S1" -> "DYS404S1a/b"), "PathFinder Plus")
  )

  val companyPrefix="YBL"

  val snpSiteNameDatas = ArrayBuffer(
    SiteNameData(Shared.snpCoreset.toSet, Map(), "coreset"),
    SiteNameData(Shared.snpN.toSet, Map(), "N"),
    SiteNameData(Shared.snpP.toSet, Map(), "P"),
    SiteNameData(Shared.snp01.toSet, Map(), "O1a"),
    SiteNameData(Shared.snp02.toSet, Map(), "O1b"),
    SiteNameData(Shared.snpGamma.toSet, Map(), "Oγ"),
    SiteNameData(Shared.snpAlpha.toSet, Map(), "Oα"),
    SiteNameData(Shared.snpBeta.toSet, Map(), "Oβ")
  )
  val allSnpSiteNames=snpSiteNameDatas.flatMap(x=>x.siteNames).distinct

  val lNames = ArrayBuffer("﻿赵", "钱", "孙", "李", "周", "吴", "郑", "王", "冯", "陈", "褚", "卫", "蒋", "沈", "韩", "杨",
    "朱", "秦", "尤", "许", "何", "吕", "施", "张", "孔", "曹", "严", "华", "金", "魏", "陶", "姜", "戚", "谢", "邹", "喻",
    "柏", "水", "窦", "章", "云", "苏", "潘", "葛", "奚", "范", "彭", "郎", "鲁", "韦", "昌", "马", "苗", "凤", "花", "方",
    "俞", "任", "袁", "柳", "酆", "鲍", "史", "唐", "费", "廉", "岑", "薛", "雷", "贺", "倪", "汤", "滕", "殷", "罗", "毕",
    "郝", "邬", "安", "常", "乐", "于", "时", "傅", "皮", "卞", "齐", "康", "伍", "余", "元", "卜", "顾", "孟", "平", "黄",
    "和", "穆", "萧", "尹", "姚", "邵", "湛", "汪", "祁", "毛", "禹", "狄", "米", "贝", "明", "臧", "计", "伏", "成", "戴",
    "谈", "宋", "茅", "庞", "熊", "纪", "舒", "屈", "项", "祝", "董", "梁", "杜", "阮", "蓝", "闵", "席", "季", "麻", "强",
    "贾", "路", "娄", "危", "江", "童", "颜", "郭", "梅", "盛", "林", "刁", "锺", "徐", "邱", "骆", "高", "夏", "蔡", "田",
    "樊", "胡", "凌", "霍", "虞", "万", "支", "柯", "昝", "管", "卢", "莫", "经", "房", "裘", "缪", "干", "解", "应", "宗",
    "丁", "宣", "贲", "邓", "郁", "单", "杭", "洪", "包", "诸", "左", "石", "崔", "吉", "钮", "龚", "程", "嵇", "邢", "滑",
    "裴", "陆", "荣", "翁", "荀", "羊", "於", "惠", "甄", "麴", "家", "封", "芮", "羿", "储", "靳", "汲", "邴", "糜", "松",
    "井", "段", "富", "巫", "乌", "焦", "巴", "弓", "牧", "隗", "山", "谷", "车", "侯", "宓", "蓬", "全", "郗", "班", "仰",
    "秋", "仲", "伊", "宫", "宁", "仇", "栾", "暴", "甘", "钭", "历", "戎", "祖", "武", "符", "刘", "景", "詹", "束", "龙",
    "叶", "幸", "司", "韶", "郜", "黎", "蓟", "溥", "印", "宿", "白", "怀", "蒲", "邰", "从", "鄂", "索", "咸", "籍", "赖",
    "卓", "蔺", "屠", "蒙", "池", "乔", "阳", "郁", "胥", "能", "苍", "双", "闻", "莘", "党", "翟", "谭", "贡", "劳", "逄",
    "姬", "申", "扶", "堵", "冉", "宰", "郦", "雍", "却", "璩", "桑", "桂", "濮", "牛", "寿", "通", "边", "扈", "燕", "冀",
    "僪", "浦", "尚", "农", "温", "别", "庄", "晏", "柴", "瞿", "阎", "充", "慕", "连", "茹", "习", "宦", "艾", "鱼", "容", "向", "古", "易", "慎", "戈", "廖", "庾", "终", "暨", "居", "衡", "步", "都", "耿", "满", "弘", "匡", "国", "文", "寇", "广", "禄", "阙", "东", "欧", "殳", "沃", "利", "蔚", "越", "夔", "隆", "师", "巩", "厍", "聂", "晁", "勾", "敖", "融", "冷", "訾", "辛", "阚", "那", "简", "饶", "空", "曾", "毋", "沙", "乜", "养", "鞠", "须", "丰", "巢", "关", "蒯", "相", "查", "后", "荆", "红", "游", "竺", "权", "逮", "盍", "益", "桓", "公", "万俟", "司马", "上官", "欧阳", "夏侯", "诸葛", "闻人", "东方", "赫连", "皇甫", "尉迟", "公羊", "澹台", "公冶", "宗政", "濮阳", "淳于", "单于", "太叔", "申屠", "公孙", "仲孙", "轩辕", "令狐", "钟离", "宇文", "长孙", "慕容", "司徒", "司空", "召", "有", "舜", "叶赫那拉", "丛", "岳", "寸", "贰", "皇", "侨", "彤", "竭", "端", "赫", "实", "甫", "集", "象", "翠", "狂", "辟", "典", "良", "函", "芒", "苦", "其", "京", "中", "夕", "之", "章佳", "那拉", "冠", "宾", "香", "果", "依尔根觉罗", "依尔觉罗", "萨嘛喇", "赫舍里", "额尔德特", "萨克达", "钮祜禄", "他塔喇", "喜塔腊", "讷殷富察", "叶赫那兰", "库雅喇", "瓜尔佳", "舒穆禄", "爱新觉罗", "索绰络", "纳喇", "乌雅", "范姜", "碧鲁", "张廖", "张简", "图门", "太史", "公叔", "乌孙", "完颜", "马佳", "佟佳", "富察", "费莫", "蹇", "称", "诺", "来", "多", "繁", "戊", "朴", "回", "毓", "税", "荤", "靖", "绪", "愈", "硕", "牢", "买", "但", "巧", "枚", "撒", "泰", "秘", "亥", "绍", "以", "壬", "森", "斋", "释", "奕", "姒", "朋", "求", "羽", "用", "占", "真", "穰", "翦", "闾", "漆", "贵", "代", "贯", "旁", "崇", "栋", "告", "休", "褒", "谏", "锐", "皋", "闳", "在", "歧", "禾", "示", "是", "委", "钊", "频", "嬴", "呼", "大", "威", "昂", "律", "冒", "保", "系", "抄", "定", "化", "莱", "校", "么", "抗", "祢", "綦", "悟", "宏", "功", "庚", "务", "敏", "捷", "拱", "兆", "丑", "丙", "畅", "苟", "随", "类", "卯", "俟", "友", "答", "乙", "允", "甲", "留", "尾", "佼", "玄", "乘", "裔", "延", "植", "环", "矫", "赛", "昔", "侍", "度", "旷", "遇", "偶", "前", "由", "咎", "塞", "敛", "受", "泷", "袭", "衅", "叔", "圣", "御", "夫", "仆", "镇", "藩", "邸", "府", "掌", "首", "员", "焉", "戏", "可", "智", "尔", "凭", "悉", "进", "笃", "厚", "仁", "业", "肇", "资", "合", "仍", "九", "衷", "哀", "刑", "俎", "仵", "圭", "夷", "徭", "蛮", "汗", "孛", "乾", "帖", "罕", "洛", "淦", "洋", "邶", "郸", "郯", "邗", "邛", "剑", "虢", "隋", "蒿", "茆", "菅", "苌", "树", "桐", "锁", "钟", "机", "盘", "铎", "斛", "玉", "线", "针", "箕", "庹", "绳", "磨", "蒉", "瓮", "弭", "刀", "疏", "牵", "浑", "恽", "势", "世", "仝", "同", "蚁", "止", "戢", "睢", "冼", "种", "涂", "肖", "己", "泣", "潜", "卷", "脱", "谬", "蹉", "赧", "浮", "顿", "说", "次", "错", "念", "夙", "斯", "完", "丹", "表", "聊", "源", "FirstName", "吾", "寻", "展", "出", "不", "户", "闭", "才", "无", "书", "学", "愚", "本", "性", "雪", "霜", "烟", "寒", "少", "字", "桥", "板", "斐", "独", "千", "诗", "嘉", "扬", "善", "揭", "祈", "析", "赤", "紫", "青", "柔", "刚", "奇", "拜", "佛", "陀", "弥", "阿", "素", "长", "僧", "隐", "仙", "隽", "宇", "祭", "酒", "淡", "塔", "琦", "闪", "始", "星", "南", "天", "接", "波", "碧", "速", "禚", "腾", "潮", "镜", "似", "澄", "潭", "謇", "纵", "渠", "奈", "风", "春", "濯", "沐", "茂", "英", "兰", "檀", "藤", "枝", "检", "生", "折", "登", "驹", "骑", "貊", "虎", "肥", "鹿", "雀", "野", "禽", "飞", "节", "宜", "鲜", "粟", "栗", "豆", "帛", "官", "布", "衣", "藏", "宝", "钞", "银", "门", "盈", "庆", "喜", "及", "普", "建", "营", "巨", "望", "希", "道", "载", "声", "漫", "犁", "力", "贸", "勤", "革", "改", "兴", "亓", "睦", "修", "信", "闽", "北", "守", "坚", "勇", "汉", "练", "尉", "士", "旅", "五", "令", "将", "旗", "军", "行", "奉", "敬", "恭", "仪", "母", "堂", "丘", "义", "礼", "慈", "孝", "理", "伦", "卿", "问", "永", "辉", "位", "让", "尧", "依", "犹", "介", "承", "市", "所", "苑", "杞", "剧", "第", "零", "谌", "招", "续", "达", "忻", "六", "鄞", "战", "迟", "候", "宛", "励", "粘", "萨", "邝", "覃", "辜", "初", "楼", "城", "区", "局", "台", "原", "考", "妫", "纳", "泉", "老", "清", "德", "卑", "过", "麦", "曲", "竹", "百", "福", "言", "第五", "佟", "爱", "年", "笪", "谯", "哈", "墨", "南宫", "赏", "伯", "佴", "佘", "牟", "商", "西门", "东门", "左丘", "梁丘", "琴", "后", "况", "亢", "缑", "帅", "微生", "羊舌", "海", "归", "呼延", "南门", "东郭", "百里", "钦", "鄢", "汝", "法", "闫", "楚", "晋", "谷梁", "宰父", "夹谷", "拓跋", "壤驷", "乐正", "漆雕", "公西", "巫马", "端木", "颛孙", "子车", "督", "仉", "司寇", "亓官", "鲜于", "锺离", "盖", "逯", "库", "郏", "逢", "阴", "薄", "厉", "稽", "闾丘", "公良", "段干", "开", "光", "操", "瑞", "眭", "泥", "运", "摩", "伟", "铁", "迮")

  val citys = ArrayBuffer("北京市", "天津市", "石家庄市", "唐山市", "秦皇岛市", "邯郸市", "邢台市", "保定市", "张家口市",
    "承德市", "沧州市", "廊坊市", "衡水市", "太原市", "大同市", "阳泉市", "长治市", "晋城市", "朔州市", "晋中市", "运城市",
    "忻州市", "临汾市", "吕梁市", "呼和浩特市", "包头市", "乌海市", "赤峰市", "通辽市", "鄂尔多斯市", "呼伦贝尔市", "巴彦淖尔市",
    "乌兰察布市", "兴安盟", "锡林郭勒盟", "阿拉善盟", "沈阳市", "大连市", "鞍山市", "抚顺市", "本溪市", "丹东市", "锦州市",
    "营口市", "阜新市", "辽阳市", "盘锦市", "铁岭市", "朝阳市", "葫芦岛市", "长春市", "吉林市", "四平市", "辽源市", "通化市",
    "白山市", "松原市", "白城市", "延边朝鲜族自治州", "哈尔滨市", "齐齐哈尔市", "鸡西市", "鹤岗市", "双鸭山市", "大庆市",
    "伊春市", "佳木斯市", "七台河市", "牡丹江市", "黑河市", "绥化市", "大兴安岭地区", "上海市", "南京市", "无锡市", "徐州市",
    "常州市", "苏州市", "南通市", "连云港市", "淮安市", "盐城市", "扬州市", "镇江市", "泰州市", "宿迁市", "杭州市", "宁波市",
    "温州市", "嘉兴市", "湖州市", "绍兴市", "金华市", "衢州市", "舟山市", "台州市", "丽水市", "合肥市", "芜湖市", "蚌埠市",
    "淮南市", "马鞍山市", "淮北市", "铜陵市", "安庆市", "黄山市", "滁州市", "阜阳市", "宿州市", "六安市", "亳州市", "池州市",
    "宣城市", "福州市", "厦门市", "莆田市", "三明市", "泉州市", "漳州市", "南平市", "龙岩市", "宁德市", "南昌市", "景德镇市",
    "萍乡市", "九江市", "新余市", "鹰潭市", "赣州市", "吉安市", "宜春市", "抚州市", "上饶市", "济南市", "青岛市", "淄博市",
    "枣庄市", "东营市", "烟台市", "潍坊市", "济宁市", "泰安市", "威海市", "日照市", "莱芜市", "临沂市", "德州市", "聊城市",
    "滨州市", "菏泽市", "郑州市", "开封市", "洛阳市", "平顶山市", "安阳市", "鹤壁市", "新乡市", "焦作市", "濮阳市", "许昌市",
    "漯河市", "三门峡市", "南阳市", "商丘市", "信阳市", "周口市", "驻马店市", "济源市", "武汉市", "黄石市", "十堰市", "宜昌市",
    "襄阳市", "鄂州市", "荆门市", "孝感市", "荆州市", "黄冈市", "咸宁市", "随州市", "恩施土家族苗族自治州", "仙桃市", "潜江市",
    "天门市", "神农架林区", "长沙市", "株洲市", "湘潭市", "衡阳市", "邵阳市", "岳阳市", "常德市", "张家界市", "益阳市", "郴州市",
    "永州市", "怀化市", "娄底市", "湘西土家族苗族自治州", "广州市", "韶关市", "深圳市", "珠海市", "汕头市", "佛山市", "江门市",
    "湛江市", "茂名市", "肇庆市", "惠州市", "梅州市", "汕尾市", "河源市", "阳江市", "清远市", "东莞市", "中山市", "东沙群岛",
    "潮州市", "揭阳市", "云浮市", "南宁市", "柳州市", "桂林市", "梧州市", "北海市", "防城港市", "钦州市", "贵港市", "玉林市",
    "百色市", "贺州市", "河池市", "来宾市", "崇左市", "海口市", "三亚市", "三沙市", "儋州市", "五指山市", "琼海市", "文昌市",
    "万宁市", "东方市", "定安县", "屯昌县", "澄迈县", "临高县", "白沙黎族自治县", "昌江黎族自治县", "乐东黎族自治县",
    "陵水黎族自治县", "保亭黎族苗族自治县", "琼中黎族苗族自治县", "重庆市", "成都市", "自贡市", "攀枝花市", "泸州市", "德阳市",
    "绵阳市", "广元市", "遂宁市", "内江市", "乐山市", "南充市", "眉山市", "宜宾市", "广安市", "达州市", "雅安市", "巴中市",
    "资阳市", "阿坝藏族羌族自治州", "甘孜藏族自治州", "凉山彝族自治州", "贵阳市", "六盘水市", "遵义市", "安顺市", "毕节市",
    "铜仁市", "黔西南布依族苗族自治州", "黔东南苗族侗族自治州", "黔南布依族苗族自治州", "昆明市", "曲靖市", "玉溪市", "保山市",
    "昭通市", "丽江市", "普洱市", "临沧市", "楚雄彝族自治州", "红河哈尼族彝族自治州", "文山壮族苗族自治州", "西双版纳傣族自治州",
    "大理白族自治州", "德宏傣族景颇族自治州", "怒江傈僳族自治州", "迪庆藏族自治州", "拉萨市", "日喀则市", "昌都市", "林芝市",
    "山南市", "那曲市", "阿里地区", "西安市", "铜川市", "宝鸡市", "咸阳市", "渭南市", "延安市", "汉中市", "榆林市", "安康市",
    "商洛市", "兰州市", "嘉峪关市", "金昌市", "白银市", "天水市", "武威市", "张掖市", "平凉市", "酒泉市", "庆阳市", "定西市",
    "陇南市", "临夏回族自治州", "甘南藏族自治州", "西宁市", "海东市", "海北藏族自治州", "黄南藏族自治州", "海南藏族自治州",
    "果洛藏族自治州", "玉树藏族自治州", "海西蒙古族藏族自治州", "银川市", "石嘴山市", "吴忠市", "固原市", "中卫市", "乌鲁木齐市",
    "克拉玛依市", "吐鲁番市", "哈密市", "昌吉回族自治州", "博尔塔拉蒙古自治州", "巴音郭楞蒙古自治州", "阿克苏地区",
    "克孜勒苏柯尔克孜自治州", "喀什地区", "和田地区", "伊犁哈萨克自治州", "塔城地区", "阿勒泰地区", "石河子市", "阿拉尔市",
    "图木舒克市", "五家渠市", "北屯市", "铁门关市", "双河市", "可克达拉市", "昆玉市", "香港城区", "澳门城区")

  def scale100(x: BigDecimal, maxScale: Int) = {
    val scale = x.scale - 2
    val finalScale = if (scale >= maxScale) maxScale else if (scale >= 0) scale else 0
    (x * 100).setScale(finalScale, RoundingMode.HALF_UP)
  }

  def getLsName(x: String) = {
    val b1 = lNames.find(y => x.startsWith(y))
    if (b1.isDefined) b1.get else x.head.toString
  }

  def getCity(x: String) = {
    val b1 = citys.find(y => x.contains(y))
    val b2 = citys.find(y => (x + "市").contains(y))
    if (b1.isDefined) b1.get else if (b2.isDefined) b2.get else ""
  }

  def nafy(x: String) = if (x == "") "" else x

  def emptyfy(x: String) = if (x == "") "" else x

  val pattern = "yyyy-mm-dd"


}
