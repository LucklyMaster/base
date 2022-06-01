package com.masterchan.mybase

import android.os.Bundle
import android.view.View
import com.masterchan.lib.ext.setOnClickListeners
import com.masterchan.lib.log.MLog
import com.masterchan.mybase.databinding.ActivityMainBinding
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger


class MainActivity : MyBaseActivity<ActivityMainBinding>(), View.OnClickListener {

    private val msg = "黄鹤楼送孟浩然之广陵\n" +
            "[作者] 李白 [朝代] 唐\n" +
            "故人西辞黄鹤楼，烟花三月下扬州。孤帆远影碧空尽，唯见长江天际流。\n" +
            "将进酒·君不见\n" +
            "[作者] 李白 [朝代] 唐\n" +
            "君不见，黄河之水天上来，奔流到海不复回。君不见，高堂明镜悲白发，朝如青丝暮成雪。人生得意须尽欢，莫使A金樽空对月。天生我材必有用，千金散尽还复来。烹羊宰牛且为乐，会须一饮三百杯。岑夫子，丹丘生，将进酒，杯莫停。与君歌...\n" +
            "送元二使安西\n" +
            "[作者] 王维 [朝代] 唐\n" +
            "渭城朝雨浥轻尘，客舍青青柳色新。劝君更尽一杯酒，西出阳关无故人。\n" +
            "小池\n" +
            "[作者] 杨万里 [朝代] 宋\n" +
            "泉眼无声惜细流，树阴照水爱晴柔。小荷才露尖尖角，早有蜻蜓立上头。\n" +
            "春日\n" +
            "[作者] 朱熹 [朝代] 宋\n" +
            "胜日寻芳泗水滨，无边光景一时新。等闲识得东风面，万紫千红总是春。\n" +
            "咏柳\n" +
            "[作者] 贺知章 [朝代] 唐\n" +
            "碧玉妆成一树高，万条垂下绿丝绦。不知细叶谁裁出，二月春风似剪刀。\n" +
            "木兰诗 / 木兰辞\n" +
            "[作者] 佚名 [朝代] 南北朝\n" +
            "唧唧复唧唧，木兰当户织。不闻机杼声，唯闻女叹息。问女何所思，问女何所忆。女亦无所思，女亦无所忆。昨夜见军帖，可汗大点兵，军书十二卷，卷卷有爷名。阿爷无大儿，木兰无长兄，愿为市鞍马，从此替爷征。东市买骏马，西市买鞍鞯...\n" +
            "元日\n" +
            "[作者] 王安石 [朝代] 宋\n" +
            "爆竹声中一岁除，春风送暖入屠苏。千门万户曈曈日，总把新桃换旧符。\n" +
            "春望\n" +
            "[作者] 杜甫 [朝代] 唐\n" +
            "国破山河在，城春草木深。感时花溅泪，恨别鸟惊心。烽火连三月，家书抵万金。白头搔更短，浑欲不胜簪。\n" +
            "望庐山瀑布\n" +
            "[作者] 李白 [朝代] 唐\n" +
            "日照香炉生紫烟，遥看瀑布挂前川。飞流直下三千尺，疑是银河落九天。\n" +
            "清明\n" +
            "[作者] 杜牧 [朝代] 唐\n" +
            "清明时节雨纷纷，路上行人欲断魂。借问酒家何处有？牧童遥指杏花村。\n" +
            "悯农\n" +
            "[作者] 李绅 [朝代] 唐\n" +
            "春种一粒粟，秋收万颗子。四海无闲田，农夫犹饿死。\n" +
            "饮湖上初晴后雨二首·其二\n" +
            "[作者] 苏轼 [朝代] 宋\n" +
            "水光潋滟晴方好，山色空蒙雨亦奇。欲把西湖比西子，淡妆浓抹总相宜。\n" +
            "静夜思\n" +
            "[作者] 李白 [朝代] 唐\n" +
            "床前明月光，疑是地上霜。举头望明月，低头思故乡。\n" +
            "古风二首 / 悯农二首\n" +
            "[作者] 李绅 [朝代] 唐\n" +
            "春种一粒粟，秋收万颗子。四海无闲田，农夫犹饿死。锄禾日当午，汗滴禾下土。谁知盘中餐，粒粒皆辛苦。\n" +
            "悯农\n" +
            "[作者] 李绅 [朝代] 唐\n" +
            "锄禾日当午，汗滴禾下土。谁知盘中餐，粒粒皆辛苦。\n" +
            "泊船瓜洲\n" +
            "[作者] 王安石 [朝代] 宋\n" +
            "京口瓜洲一水间，钟山只隔数重山。春风又绿江南岸，明月何时照我还？\n" +
            "春夜喜雨\n" +
            "[作者] 杜甫 [朝代] 唐\n" +
            "好雨知时节，当春乃发生。随风潜入夜，润物细无声。野径云俱黑，江船火独明。晓看红湿处，花重锦官城。\n" +
            "村居\n" +
            "[作者] 高鼎 [朝代] 清\n" +
            "草长莺飞二月天，拂堤杨柳醉春烟。儿童散学归来早，忙趁东风放纸鸢。\n" +
            "芙蓉楼送辛渐\n" +
            "[作者] 王昌龄 [朝代] 唐\n" +
            "寒雨连江夜入吴，平明送客楚山孤。洛阳亲友如相问，一片冰心在玉壶。"

    private val msg2 =
        "我大声叫来邻居家的小姐姐。她想和我打雪仗，我满口答应。要知道，她一个小女子，在实力上肯定远不如我，我才不会怕她呢！嬉笑中，我乘她不备，搞起了偷袭。哈，一下子就打中了！我的心也随着雪球的爆绽乐开了花。可是，小姐姐似乎火了，一边扔雪球，一边说：“你还算不算一个男子汉，竞玩偷袭，看我今天不把你打得满地找牙！”我反驳道：“两雄相争，没有君子！”正当我在讲“大道理”时，小姐姐却发起了“反攻”，冷不丁扔出一个雪球。我躲闪不及，中弹了。但是雪球打在身上，毫无痛感，仿佛是在挠痒痒。于是，我对小姐姐做个鬼脸，嘻嘻笑道：“打得好，打得好，好像是你在给我挠痒痒！”"

    override fun onCreated(savedInstanceState: Bundle?) {
        setOnClickListeners(this, binding.keyboard, binding.imageUtils)
        Logger.addLogAdapter(AndroidLogAdapter())
    }

    override fun onClick(v: View?) {
        val clazz = when (v) {
            binding.keyboard -> AutoFoldKeyboardActivity::class.java
            binding.imageUtils -> ImageUtilsActivity::class.java
            else -> MainActivity::class.java
        }
        // startActivity(clazz)
        MLog.d("我们都有一个家")

        // MLog.d("返回结果为空，请检查网络，the result is error，please check you network")
        // MLog.e("返回结果为空，请检查网络，the result is error，please check you network",NullPointerException())
        // MLog.d(msg)
        // MLog.d(msg2)
    }

    override fun getViewBinding(): ActivityMainBinding {
        return ActivityMainBinding.inflate(layoutInflater)
    }
}