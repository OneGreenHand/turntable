# turntable
 大转盘-基于[LuckPan](https://github.com/Nipuream/LuckPan)修改(已适配转盘数：2-10)
## 效果图
![效果图](https://github.com/OneGreenHand/turntable/blob/master/img/result.jpg?raw=true)
## 使用说明
下载运行项目或[下载Apk](https://github.com/OneGreenHand/turntable/blob/master/apk/app-release.apk)
### 其他
```
1、设置数据
setNames(List<String> names) ；//仅设置文字
setImgs(List<Bitmap> imgs) ；//仅设置图片
setDatas(List<String> names, List<Bitmap> imgs) ；//图片和文字
2、转盘使用网络图片
//方式一
   Glide.with(this).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {//将网络图片转为bitmap
            @Override
        public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition){
                //TODO do something
            }
        });
//方式二
    Glide.with(mContext)//将图片地址转化为bitmap,io线程
                .asBitmap()
                .load(s)
                .submit(40, 40)//建议指定尺寸大小(单位为px)
                .get();
//方式三
使用okhttp
```
### 参数
|  方法名  |  描述  |  参数值【默认】  | 所属  |
| :--------: | :--------:| :--------:|:--: |
| LPColor1  | 转盘色值1 |   #FE645A   | 转盘内盘   |
| LPColor2 |  转盘色值2  |  #169C2D  |转盘内盘   |
| LPColor3 |  转盘色值3(转盘奇数有效)  | #FEB043   |转盘内盘   |
| LPTextColor |    文本颜色  | 白色  |转盘内盘   |
| LPTextSize  |    文本大小 | 16  |转盘内盘   |
| --- | ---|---|--- |
| LPLBgColor  |    背景色 | #F0F2F5  |转盘外盘   |
| LPLColor1  |    闪烁色值1 | 白色  |转盘外盘   |
| LPLColor2  |    闪烁色值2 | #FE645A  |转盘外盘   |

## About Me
* **Email**: <blue-hair@foxmail.com>
* **CSDN**: <https://blog.csdn.net/a295268305>