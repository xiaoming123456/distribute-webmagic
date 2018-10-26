基于webmagic的分布式机器人爬虫，主要针对电商爬虫。

1.增加组件webmagic-queue，基于netty通讯组件，内置ehcache缓存。
QueueServer类作为任务队列服务，QueueClient类作为请求端访问，TaskQueue作为任务集合

2.重写组件webmagic-selenium，基本selenium的机器人爬虫，主要方式是调用浏览器内核来执行js。
和webmagic-queue组件集成分布式机器人爬虫，且有优雅退出方式为‘exit’,定时任务收集数据入库，原生PageProcessor的升级版SeleniumProcessor，更加简单使用。针对电商规格的普通标签，单选框，多选框，下拉框，等情况的爬取已做处理，严格意义只用会js获取标签名，便可以爬虫入库。

selenium启动需要相应的浏览器插件

distribute-webmagic 一个只要用js就可以爬取电商信息的分布式框架

webmagic-queue 详解 https://blog.csdn.net/qq_31005107/article/details/83417310


线性执行的没有什么问题，应该会有些bug，欢迎小伙伴给我反馈，包括建议哟~ 
邮箱294610768@qq.com
