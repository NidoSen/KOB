主要是给个人复习用

---

## 2. 配置git环境与项目创建

### *代码仓库地址*

https://github.com/NidoSen/KOB/tree/d0873a0660557764829860b32f5e38247ad3d535

### 2.1 项目介绍

- 贪吃蛇对战游戏，游戏名是King of Bots，建成KoB，每个用户（user）可以创建任意多个bot（可以是AI，也可以是人来控制，因此有三种模式：机器对机器，人机，人人），每个用户同一时间最多派出一名bot参战
- 游戏开始后，两个bot各控制一条蛇，回合制，两条蛇各有三个方向（前左右）可走，当一方撞墙或者撞到自己的时候，另一方获胜，游戏结束
- 游戏结束后更新双方的天梯分和排名等
- 项目的扩展性：换游戏内容

### 2.2 King of Bots 项目结构

- PK模块
  - 匹配界面（微服务）
  - 实况直播界面（WebSocket协议）（后台运行匹配的两个bot的代码，每个回合两个代码得出结果后，把结果返回到前端，前端在网页里模拟出来）
  - 真人PK界面（WebSocket协议）（拓展性：加上聊天框）
- 对战列表模块
  - 录像回放
- 排行榜模块
  - Bot列表
- 用户中心模块
  - 注册
  - 登录
  - 我的Bot
  - Bot的记录

页面布局初步设计如下：

<img src="myResources\2.1 页面布局设计.png" style="zoom: 50%;" />

### 2.3 配置git环境

这部分在Linux基础课学了，直接跳过（doge）

### 2.4 前后端不分离和前后端分离

用户使用应用的本质：客户端发送一个URL，服务器端向客户端返回一个字符串（本质是函数调用）

通常来讲，会先生成一个静态html，然后来自后端的动态数据需要加入到这个html里，渲染出最终的页面，因此所谓的渲染，本质就是字符串拼接，将后端返回的数据插入到html的某些部分，如果拼接在用户浏览器用JavaScript实现（也就是后端提供数据，渲染由前端完成），为前后端分离，如果在后端用Java实现的（相当于直接把渲染完成的界面发给前端），则是前后端不分离

本项目采用前后端分离，这也是目前主流的选择

### 2.5 创建项目后端

用Idea新建项目，改下组、工件的名字，Java版本选8，JDK选1.8

Spring Boot版本选3以下，否则可能出问题；依赖项选择`Web/Spring Web`和`Template Engines/Thymeleaf`（实际上`Template Engines/Thymeleaf`可以不加，因为这个只是y总演示前后端不分离用的）

后端的端口需要改下，从8080改成3000，因为Vue3默认的端口也是8080

关于注解：会用就行（知道哪里加哪个注解），面试靠八股（我个人最后还是去学了下，加深理解）

### 2.6 创建项目前端

本项目使用VSCode写代码，因此需要下载VSCode

vue脚手架的安装和使用直接参考y总的讲义和评论区各位大佬的发言，这里做点补充：

- Nodejs装LTS版本
- vue图形化界面创建新项目需要把 初始化git仓库 取消，后面选择Vue3创建
- web项目
  - 项目创建成功后，需要安装vue-router插件和vuex插件，并安装jquery依赖和bootstrap依赖
  - 任务处在仪表盘点运行即可启动项目，再到输出处即可获取网页链接
- acapp项目
  - 项目创建成功后，需要安装vuex插件

web项目的初始化工作：

- URL一开始会在中间有个#号，需要对router/index.js文件进行调整，将两个地方的`createWebHashHistroy`都改成`createWebHistroy`，另外只改一个可能会导致闪退

- 把除了根组件App.vue外的其他vue文件都删了，App.vue和index.js也要清理无关内容

  App.vue

  ```vue
  <template>
    <router-view />
  </template>
  
  <script>
  </script>
  
  <style>
  </style>
  ```

  router/index.js

  ```js
  import { createRouter, createWebHistory } from 'vue-router'
  
  const routes = []
  
  const router = createRouter({
    history: createWebHistory(),
    routes
  })
  
  export default router
  ```

### 2.7 y总给的一个简单的代码示例

web前端 App.vue

```vue
<template>
  <div>
    <!-- html初始执行，其他部分都正常执行（静态部分），而bot_name和bot_rating要等js执行完，从后端拿到数据才能渲染（动态部分） -->
    <!-- 将js里的变量呈现在前端网页上，需要加上两层大括号 -->
    <div>Bot昵称：{{ bot_name }}</div>
    <div>Bot战力：{{ bot_rating }}</div>
  </div>
  <router-view></router-view>
</template>

<script>
import $ from 'jquery'; //使用ajax从后端取数据，因此需要导入jquery（类似功能的还有axios）
import { ref } from 'vue'; //响应式变量的创建需要用到ref

export default {
  name: "APP",
  setup: () => { //html执行完后，立刻会从上到下执行这段代码，从后端获取Bot昵称和Bot战力
    //Bot昵称和Bot战力的初始化
    let bot_name = ref("");
    let bot_rating = ref("");

    $.ajax({ //熟悉下ajax的用法
      //注意，这里会出现跨域问题，因为前端的端口号是8080，而ajax访问的网址端口号是3000
      url: "http://127.0.0.1:3000/pk/getbotinfo/",
      type: "get",
      success: resp => { //这里的resp就是后端返回的bot1
        //将bot1的值赋给Bot昵称和Bot战力，注意响应式变量在js里要用到值时，必须加上.value
        bot_name.value = resp.name;
        bot_rating.value = resp.rating;
      }
    });

    return { //要在本组件的template或其他组件使用的响应式变量、函数等，需要return了才能使用
      bot_name,
      bot_rating
    }
  }
}
</script>

<style>
body {
  background-image: url("@/assets/maitian.jpg");
  background-size: cover; /* 百分百填充 */
}
</style>
```

后端 controller/pk/BotInfoController.java

```java
package com.kob.backend.controller.pk;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pk/")
public class BotInfoController {
    
    //前端输入网址"http://127.0.0.1:3000/pk/getbotinfo/"，后端会返回bot1，bot1有两个内容：bot1["name"]="tiger"，bot1["rating"]="1500"
    @RequestMapping("getbotinfo/")
    public Map<String, String> getBotInfo(){
        Map<String, String> bot1 = new HashMap<>();
        bot1.put("name", "tiger");
        bot1.put("rating", "1500");
        return bot1;
    }
}
```

后端需要加一个类解决跨域（CORS）问题：

后端 config/CorsConfig.java

```java
package com.kob.backend.config;

import org.springframework.context.annotation.Configuration;

// Java17要用jakarta.servlet，因为javax.servlet已经舍弃
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class CorsConfig implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) res;
        HttpServletRequest request = (HttpServletRequest) req;

        String origin = request.getHeader("Origin");
        if(origin!=null) {
            response.setHeader("Access-Control-Allow-Origin", origin);
        }

        String headers = request.getHeader("Access-Control-Request-Headers");
        if(headers!=null) {
            response.setHeader("Access-Control-Allow-Headers", headers);
            response.setHeader("Access-Control-Expose-Headers", headers);
        }

        response.setHeader("Access-Control-Allow-Methods", "*");
        response.setHeader("Access-Control-Max-Age", "3600");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) {

    }

    @Override
    public void destroy() {
    }
}
```

---

## 3. 创建游戏与菜单页面

### *第1部分代码仓库地址*

https://github.com/NidoSen/KOB/tree/c5765c7b6a2960915756ae38eaba18e971d95d66

### 3.1 单个页面布局和创建公共组件导航栏NavBar.vue

本项目每个页面的布局如下，最上方为导航栏，下面的内容区放每个页面自己的内容，能通过点击导航栏切换页面

<img src="myResources\3.1 各个页面基本布局.png" style="zoom:50%;" />

因为导航栏是每个页面都会有的，变化的是内容区，所以导航栏应该专门提取为一个组件，并放到根组件

导航栏组件为NavBar.vue，在components文件夹下（每个组件必须有至少两个大写字母）

新建组件NavBar.vue并初始化，其中scoped的作用是控制样式只在当前组件起效

为了实现导航栏，需要用到bootstrap，其官网为https://v5.bootcss.com/，借助这个网站，程序员将能够实现美工的工作，即使前端的外观更好看，本项目的导航栏代码，在官网搜navbar能搜到各种样例，选择合适的拿来用即可

```vue
<template>
<!-- 这里放搜到的NavBar模板 -->
...
</template>

<script>
</script>

<style scoped>
</style>
```

将其导入根组件App.vue，这样每个页面都会出现导航栏：

```vue
<template>
  <NavBar /> <!-- 使用NavBar组件，在页面最上方出现导航栏 -->
  <router-view></router-view>
</template>

<script>
import NavBar from "./components/NavBar.vue"; //导入NavBar组件
// 因为Navbar用到了bootstrap，所以还需要导入bootstrap的相关依赖
import "bootstrap/dist/css/bootstrap.min.css";
import "bootstrap/dist/js/bootstrap";

export default {
  components: {
    NavBar, //这一步的意思是要用到NavBar这个组件
  },
};
</script>

<style>
body {
  background-image: url("@/assets/maitian.jpg");
  background-size: cover; /* 百分百填充 */
}
</style>
```

另外这里会出现一个bug，需要安装`@popperjs/core`依赖

### 3.2 导航栏的跳转功能实现

导航栏从左到右的内容分别是King Of Bots，对战，对局列表，排行榜和用户

<img src="myResources\3.2 导航栏内容.png" style="zoom:50%;" />

导航栏需要实现点击各个部分能自动跳转到对应页面，因此需要完成以下步骤：

1. 在views文件夹下创建相应的模块，整体目录结构如下：

   - views
     - error
       - NotFound.vue
     - pk
       - PkIndexView.vue
     - ranklist
       - RanklistIndexView.vue
     - record
       - RecordIndexView.vue
     - user
       - bots
         - UserBotIndexView.vue

2. 实现跳转与router-view有关，它能实现自动根据网址跳转，具体如何跳转在router/index.js定义，需要修改routes的内容

   ```js
   import { createRouter, createWebHistory } from 'vue-router'
   import PkIndexView from '../views/pk/PkIndexView.vue'
   import RecordIndexView from '../views/record/RecordIndexView.vue'
   import RanklistIndexView from '../views/ranklist/RanklistIndexView.vue'
   import UserBotIndexView from '../views/user/bots/UserBotIndexView.vue'
   import NotFound from '../views/error/NotFound.vue'
   
   const routes = [
     {
       // 如果是根路径，重定向到pk页面
       path: "/",
       name: "home",
       redirect: "/pk/"
     },
     {
       path: "/pk/",
       name: "pk_index",
       component: PkIndexView,
     },
     {
       path: "/record/",
       name: "record_index",
       component: RecordIndexView,
     },
     {
       path: "/ranklist/",
       name: "ranklist_index",
       component: RanklistIndexView,
     },
     {
       path: "/user/bot/",
       name: "user_bot_index",
       component: UserBotIndexView,
     },
     {
       path: "/404/",
       name: "not_found",
       component: NotFound,
     },
     {
       //所有不符合规范的链接，都重定向到404页面
       path: "/:catchAll()",
       redirect: "/404/"
     }
   ]
   
   const router = createRouter({
     history: createWebHistory(),
     routes
   })
   
   export default router
   ```

3. 实现跳转的第二步是改NavBar.vue文件

   ```vue
   ...
   <a class="navbar-brand" href="/">King of Bots</a>
   ...
   <li class="nav-item">
       <a class="nav-link" aria-current = "page" href="/pk/">对战</a>
   </li>
   <li class="nav-item">
       <a class="nav-link" href="/record/">对局列表</a>
   </li>
   <li class="nav-item">
       <a class="nav-link" href="/ranklist/">排行榜</a>
   </li>
   ...
   <li>
       <a class="dropdown-item" href="/user/bot/">我的Bot</a>
   </li>
   ```

4. 上面这么做了，跳转时页面会自动刷新，若想实现不刷新，需要进一步修改，用router-link替换a

   ```vue
   ...
   <router-link class="navbar-brand" :to = "{name: 'home'}">King of Bots</router-link>
   ...
   <li class="nav-item">
       <router-link class="nav-link" :to = "{name: 'pk_index'}">对战</router-link>
   </li>
   <li class="nav-item">
       <router-link class="nav-link" :to = "{name: 'record_index'}">对局列表</router-link>
   </li>
   <li class="nav-item">
       <router-link class="nav-link" :to = "{name: 'ranklist_index'}">排行榜</router-link>
   </li>
   ...
   <li>
       <router-link class="dropdown-item" :to = "{name: 'user_bot_index'}">我的Bot</router-link>
   </li>
   ```

5. 最后一步是实现聚焦，即跳转到对应页面后，将导航栏对应部分高亮，具体的实现需要先用useRoute获得当前的Routes

   ```vue
   <template>
   ...
   <li class="nav-item">
       <!-- active是聚焦（高亮）的意思 -->
       <!-- 原本的class前面是没有冒号的，加上以后表示class后面跟的是一条可运行的语句，需要运行完获得结果，且里面只有用单引号包围的内容才是字符串，其他的是可运行语句及其变量，比如这里的route_name就是在下面的js部分定义的变量，而不是字符串-->
       <router-link :class="route_name == 'pk_index' ? 'nav-link active' : 'nav-link'" :to = "{name: 'pk_index'}">对战</router-link>
   </li>
   <li class="nav-item">
       <router-link :class="route_name == 'record_index' ? 'nav-link active' : 'nav-link'" :to = "{name: 'record_index'}">对局列表</router-link>
   </li>
   <li class="nav-item">
       <router-link :class="route_name == 'ranklist_index' ? 'nav-link active' : 'nav-link'" :to = "{name: 'ranklist_index'}">排行榜</router-link>
   </li>
   ...
   <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
       Nido
   </a>
   ...
   </template>
   
   <script>
   import { useRoute } from 'vue-router' //用于获取routes
   import { computed } from 'vue' //实时计算
   
   export default {
     setup() {
       const route = useRoute();
       let route_name = computed(() => route.name)
       return {
         route_name
       }
     }
   }
   </script>
   
   <style scoped>
   </style>
   ```

NavBar.vue的完整代码如下：

```vue
<template>
<!-- navbar-light浅色导航栏，navbar-dark深色导航栏 -->
<nav class="navbar navbar-expand-lg navbar-dark bg-dark">
  <!-- container-fluid宽一点的导航栏，container窄一点的导航栏 -->
  <div class="container">
    <router-link class="navbar-brand" :to = "{name: 'home'}">King of Bots</router-link>
    <div class="collapse navbar-collapse" id="navbarText">
      <ul class="navbar-nav me-auto mb-2 mb-lg-0">
        <li class="nav-item">
          <!-- active是聚焦（高亮）的意思 -->
          <router-link :class="route_name == 'pk_index' ? 'nav-link active' : 'nav-link'" :to = "{name: 'pk_index'}">对战</router-link>
        </li>
        <li class="nav-item">
          <router-link :class="route_name == 'record_index' ? 'nav-link active' : 'nav-link'" :to = "{name: 'record_index'}">对局列表</router-link>
        </li>
        <li class="nav-item">
          <router-link :class="route_name == 'ranklist_index' ? 'nav-link active' : 'nav-link'" :to = "{name: 'ranklist_index'}">排行榜</router-link>
        </li>
      </ul>
      
      <!-- 这里实现的是右边的用户中心下拉菜单，其中删掉me-auto mb-2 mb-lg-0能控制它出现在最右侧-->
      <ul class="navbar-nav">
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
            Nido
          </a>
          <ul class="dropdown-menu">
            <li>
              <router-link class="dropdown-item" :to = "{name: 'user_bot_index'}">我的Bot</router-link>
            </li>
            <li><hr class="dropdown-divider"></li>
            <li><a class="dropdown-item" href="#">退出</a></li>
          </ul>
        </li>
      </ul>
    </div>
  </div>
</nav>
</template>

<script>
import { useRoute } from 'vue-router'
import { computed } from 'vue'

export default {
  setup() {
    const route = useRoute();
    let route_name = computed(() => route.name)
    return {
      route_name
    }
  }
}
</script>

<style scoped>
</style>
```

### 3.3 创建公共组件内容区ContentField.vue

一般来讲，各个页面的内容区最好限定在一个范围内（类似AcWing的主页），因此需要创建一个公共组件ContentField.vue，这需要用到bootstrap的card组件

ContentField.vue的完整代码如下：

```vue
<template>
	<!-- 输入"div.container>div.card>div.card-body"能快速自动生成 -->
    <div class="container content-field">
        <div class="card">
            <div class="card-body">
                <!-- slot的作用类似传参，其他组件使用ContentField时能进行填充，具体例子见下文 -->
                <slot></slot>
            </div>
        </div>
    </div>
</template>

<script>
</script>

<style scoped>
/* 让内容区整体往下移一点，和导航栏有一点距离 */
div.content-field {
    margin-top: 20px;
}
</style>
```

以排行榜为例，ContentField组件的用法如下：

```vue
<template>
    <ContentField> <!-- slot能让ContentField的用法和div差不多-->
        排行榜
    </ContentField>
</template>

<script>
//先导入
import ContentField from '../../components/ContentField.vue'

export default {
    components: {
        ContentField //声明要使用
    }
}
</script>

<style scoped>
</style>
```

### 3.4 地图的设计和动画的实现原理

地图长这样：

<img src="myResources\3.3 地图.png" style="zoom:50%;" />

- 周围一圈被墙包围
- 中间有许多障碍物，且是沿着对角线对称的（为了避免平局，下一节课改成了中心对称）
- 初始两条蛇分别出现在左下角和右上角
- 要保证两条蛇的起点是连通的（两条蛇的起点不能有障碍物，且障碍物不能导致两蛇无法遇见）
- 每次刷新得到一个新的地图
- 实现地图的对象只能被创建一次

如何实现蛇动起来（动画原理）：

- 游戏中的每个部分作为一个类，有一个公共的基类，这个基类中有start，update等相关函数
- 实现每秒刷新60帧
- 每次刷新时，调用所有游戏对象的start函数或update函数，更新每个游戏对象的属性（大小，位置等）
- 这样每一帧渲染一次，就形成的动画效果

游戏脚本的目录结构：

- assets
  - images
  - scripts
    - AcGameObject.js（基类）
    - Cell.js
    - GameMap.js
    - Snake.js
    - Wall.js

### 3.5 游戏公共部分AcGameObject

AcGameObject.js实现了游戏的公共部分，即基类和每秒刷新n帧：

- 第1部分，实现游戏基类，包括constructor构造函数，start函数，update函数和destroy销毁函数
- 第2部分，实现每秒n帧，每一帧更新每个游戏对象，具体的思路：构造一个不断调用自身的函数step，该函数先完成当前时间下每个游戏对象的属性更新，再使用requestAnimationFrame函数，在下一帧继续执行step
- 这样就会出现 requestAnimationFrame->step->requestAnimationFrame->step->... 不断执行的情况，保证每隔一段时间就更新一次动画

完整代码：

```js
//第1部分，实现游戏基类
const AC_GAME_OBJECTS = []; // 存储所有游戏对象

export class AcGameObject {
    constructor() { // 构造函数
        AC_GAME_OBJECTS.push(this);
        this.timedelta = 0; // 当前帧和上一帧执行的间隔，因为帧与帧之间的时间间隔不一定是均匀的，所以需要记录时间间隔，配合恒定的速度确定移动的距离，实现匀速移动
        this.has_called_start = false; // 记录是否执行过
    }

    start() {  // 只执行一次
    }

    update() {  // 每一帧执行一次，除了第一帧之外
    }

    on_destroy() {  // 删除之前执行
    }

    destroy() {
        this.on_destroy(); // 删除之前执行回调函数

        for (let i in AC_GAME_OBJECTS) {
            const obj = AC_GAME_OBJECTS[i];
            if (obj === this) { // 从所有游戏对象中找到当前对象，删除
                AC_GAME_OBJECTS.splice(i);
                break;
            }
        }
    }
}

// 第2部分，step函数实现每秒n帧，每一帧都更新所有游戏对象的属性
let last_timestamp;  // 上一次执行的时刻
const step = timestamp => { // 传入的函数是当前执行的时刻
    // 先更新所有游戏对象的属性（start或update）
    for (let obj of AC_GAME_OBJECTS) {
        if (!obj.has_called_start) {
            obj.has_called_start = true;
            obj.start();
        } else {
            obj.timedelta = timestamp - last_timestamp;
            obj.update();
        }
    }
    last_timestamp = timestamp;
    
    // 下一帧使用requestAnimationFrame函数调用自身
    requestAnimationFrame(step)
}

// 运行函数，开始动画
requestAnimationFrame(step)
```

### 3.6 地图类GameMap及相关组件

使用地图类的基本思路：

- pk界面（PkIndexView.vue）不适用ContentField（它出现的是一整个白框，pk界面展示游戏地图，且可能游戏结束会有结算界面），因此需要写一个专门的组件components/PlayGround.vue，同时components/PlayGround.vue可能会调用游戏地图、结算界面等组件，所以需要继续写专门的组件，这里先实现了游戏地图组件components/GameMap.vue

- 因此思路为：PkIndexView.vue调用PlayGround.vue获取各种游戏界面，目前实现的其中一个游戏界面组件为GameMap.vue，因此PlayGround.vue需要调用GameMap.vue，然后GameMap.vue使用钩子函数在页面挂载完成后使用GameMap.js生成地图对象

- 后续每一帧，地图对象的父元素都记录了当前窗口的大小，实时调整游戏界面大小

- PkIndexView.vue

  ```vue
  <template>
      <div>
          <PlayGround />
      </div>
  </template>
  
  <script>
  import PlayGround from '../../components/PlayGround.vue'
  
  export default {
      components: {
          PlayGround
      }
  }
  </script>
  
  <style scoped>
  </style>

- PlayGround.vue

  ```vue
  <template>
      <div class="playground">
          <!-- 导入并使用游戏地图组件 -->
          <GameMap />
      </div>
  </template>
  
  <script>
  import GameMap from './GameMap.vue'
  
  export default {
      components: {
          GameMap,
      }
  }
  </script>
  
  <style scoped>
  div.playground {
      /* 实现游戏区域大小的动态调整 */
      width: 60vw; /* 60%的窗口宽度 */
      height: 70vh; /* 70%的窗口高度 */
      margin: 40px auto; /* 上方上边距为40px，左右居中 */
  }
  </style>

- GameMap.vue

  ```vue
  <template>
  	<!-- class="gamemap"，起名和文件名一样，挺有特异性 -->
      <div ref="parent" class="gamemap">
          <!-- canvas标签为画布 -->
          <canvas ref="canvas"></canvas>
      </div>
  </template>
  
  <script>
  import { GameMap } from "@/assets/scripts/GameMap";
  import { ref, onMounted } from 'vue'
  
  export default {
      setup() {
          let parent = ref(null); //父元素，上面的 ref="parent" 能够让父元素指向div
          let canvas = ref(null); //画布，上面的 ref="canvas" 能够让画布指向canvas标签
          
          //使用钩子函数是因为要相等页面挂载完成，也就是html部分的代码都执行完了（即关键的两条语句 ref="parent" 和 ref="canvas" 已经执行完了）
          onMounted(() => { //挂载完之后，再创建游戏对象
              new GameMap(canvas.value.getContext('2d'), parent.value)
          });
  
          return {
              parent,
              canvas
          }
      }
  }
  </script>
  
  <style scoped>
  div.gamemap {
      /* 宽度和高度都100%，和父元素大小相同，保持游戏区域不缩水 */
      width: 100%;
      height: 100%;
      display: flex;
      justify-content: center; /* 水平居中 */
      align-items: center; /* 竖直居中 */
  }
  </style>
  ```

GameMap.js

```js
import { AcGameObject } from "./AcGameObject";
import { Wall } from "./Wall";

export class GameMap extends AcGameObject {
    constructor(ctx, parent) { //ctx是画布，parent是父元素，用于确定长宽
        super(); // 执行基类构造函数

        this.ctx = ctx;
        this.parent = parent;
        this.L = 0; //小正方形的边长，由update_size更新
        
        //游戏地图是一个13*13的大正方形，由169个小正方形组成
        this.rows = 13;
        this.cols = 13;

        this.inner_walls_count = 30; //内部障碍物的数量
        this.walls = [];
    }
    
    //障碍物连通性检验函数
    check_connectivity(g, sx, sy, tx, ty) { //sx和sy为起点坐标，tx和ty为终点坐标
        if (sx == tx && sy == ty) return true;
        g[sx][sy] = true;

        let dx = [-1, 0, 1, 0], dy = [0, 1, 0, -1];
        for (let i = 0; i < 4; i++) {
            let x = sx + dx[i], y = sy + dy[i];
            if (!g[x][y] && this.check_connectivity(g, x, y, tx, ty)) {
                return true;
            }
        }
        return false;
    }

    create_walls() {
        const g = [];
        for (let r = 0; r < this.rows; r++) {
            g[r] = [];
            for (let c = 0; c < this.cols; c++) {
                g[r][c] = false;
            }
        }

        //给四周加上围墙
        for (let r = 0; r < this.rows; r ++) {
            g[r][0] = g[r][this.cols - 1] = true;
        }
        for (let c = 0; c < this.cols; c++) {
            g[0][c] = g[this.rows - 1][c] = true;
        }

        //创建随机障碍物
        for (let i = 0; i < this.inner_walls_count / 2; i++) { //因为地图是对称的，所以只需要生成count/2次
            for (let j = 0; j < 1000; j++) { //试1000次，有一次找到空余位置就结束循环
                let r = parseInt(Math.random() * this.rows);
                let c = parseInt(Math.random() * this.cols);
                if (g[r][c] || g[c][r]) continue; //当前位置获对角线对称位置已经被占了
                //障碍物不能是两条蛇的起点
                if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2) continue;

                g[r][c] = g[c][r] = true;
                break;
            }
        }
        
        //障碍物创建完后还需要检查连通性
        //为了避免连通性检验导致改变已经记录的障碍物位置，需要进行深度拷贝，传参传的是拷贝后的障碍物位置
        const copy_g = JSON.parse(JSON.stringify(g));
        if (!this.check_connectivity(copy_g, this.rows - 2, 1, 1, this.cols - 2))
            return false;
        
        //最后根据记录的障碍物位置，创建障碍物
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if (g[r][c]) {
                    this.walls.push(new Wall(r, c, this))
                }
            }
        }

        return true;
    }

    start() {
        //尝试1000次，只要有一次创建的所有障碍物是符合要求的（不占起点且连通），就退出
        for (let i = 0; i < 1000; i++) {
            if (this.create_walls()) {
                break;
            }
        }
    }
    
    //根据窗口大小调整画布大小
    update_size() {
        //parseInt取整，避免浮点数问题导致小正方形之间存在缝隙
        this.L = parseInt(Math.min(this.parent.clientWidth / this.cols, this.parent.clientHeight / this.rows));
        this.ctx.canvas.width = this.L * this.cols;
        this.ctx.canvas.height = this.L * this.rows;
    }

    update() {
        this.update_size(); //因为窗口大小可能改变，所以需要调整
        this.render(); //每一帧渲染一次
    }
    
    //渲染函数
    render() {
        const color_even = "#AAD751", color_odd = "#A2D149";
        for (let r = 0; r < this.rows; r++) {
            for (let c = 0; c < this.cols; c++) {
                if ((r + c) % 2 == 0) {
                    this.ctx.fillStyle = color_even;
                } else {
                    this.ctx.fillStyle = color_odd;
                }
                this.ctx.fillRect(c * this.L, r * this.L, this.L, this.L); //左上角是0，0
            }
        }
    }
}
```

Wall.js

```js
import { AcGameObject } from "./AcGameObject";

export class Wall extends AcGameObject {
    constructor(r, c, gamemap) {
        super();

        this.r = r;
        this.c = c;
        this.gamemap = gamemap;
        this.color = "#B37226";
    }

    update() {
        this.render();
    }

    render() {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;

        ctx.fillStyle = this.color;
        ctx.fillRect(this.c * L, this.r * L, L, L);
    }
}
```

墙和障碍物能盖住地图的原因：网页的渲染是根据AC_GAME_OBJECTS数组的顺序来的，因为地图在AC_GAME_OBJECTS数组中的顺序在墙和障碍物的前面，所以墙和障碍物能覆盖地图

### *第2部分代码仓库地址*

https://github.com/NidoSen/KOB/tree/7612cb711988ed775b1e885f65235502aad4e6a5

### 3.7 地图的改进

要避免两条蛇在同一时间走到一个格子，可以通过控制行数和列数相加为奇数，则两条蛇的横纵坐标之和在任意时刻将分别为一个奇数和一个偶数，于是永远不会相遇

同时地图应该改为中心对称的，因为行数和列数相加为奇数，就不存在沿对角线对称的图

```js
//创建随机障碍物
for (let i = 0; i < this.inner_walls_count / 2; i++) { //因为地图是对称的，所以只需要生成count/2次
    for (let j = 0; j < 1000; j++) { //试1000次，有一次找到空余位置就结束循环
        
        let r = parseInt(Math.random() * this.rows);
        let c = parseInt(Math.random() * this.cols);
        if (g[r][c] || g[this.rows - 1 - r][this.cols - 1 - c]) continue; //当前位置或中心对称位置已经被占了
        //障碍物不能是两条蛇的起点
        if (r == this.rows - 2 && c == 1 || r == 1 && c == this.cols - 2)
            continue;

        g[r][c] = g[this.rows - 1 - r][this.cols - 1 - c] = true;
        break;
    }
}
```

仍需优化的地方：目前地图的生成还是完全依赖于前端，但可能存在公平性问题，后期应该将地图的生成移交给后端

### 3.8 蛇的设计和实现

控制蛇的增长速度：前十步每走一步长度加一，之后每三步长度加一

蛇是一堆格子的序列，可以先实现一个类，记录格子的位置和中心

Cell.js

```js
export class Cell {
    constructor(r, c) {
        this.r = r; // 格子左上角行坐标
        this.c = c; // 格子左上角列坐标
        this.x = c + 0.5; // 格子中心行坐标
        this.y = r + 0.5; // 格子中心列坐标
    }
}
```

Snake.js

```js
import { AcGameObject } from "./AcGameObject";
import { Cell } from "./Cell";

export class Snake extends AcGameObject {
    constructor(info, gamemap) {
        super();

        this.id = info.id;
        this.color = info.color;
        this.gamemap = gamemap;

        this.cells = [new Cell(info.r, info.c)]; //存放蛇的身体，cells[0]存放蛇头，初始的时候只有蛇头
        this.next_cell = null; //下一步的目标位置

        this.speed = 5; //蛇每秒走5个格子
        this.direction = -1; //-1表示没有指令，0、1、2、3/表示上右下左
        this.status = "idle"; //idle表示静止，move表示正在移动，die表示死亡

        this.dr = [-1, 0, 1, 0]; //4个方向行的偏移量
        this.dc = [0, 1, 0, -1]; //4个方向列的偏移量

        this.step = 0; //表示回合数
        this.eps = 1e-2; //允许的误差

        this.eye_direction = 0;
        if (this.id === 1) this.eye_direction = 2; //左下角的蛇初始朝上，右上角的蛇朝下

        this.eye_dx = [ //蛇眼睛不同方向的x的偏移量
            [-1, 1],
            [1, 1],
            [1, -1],
            [-1, -1],
        ];
        this.eye_dy = [ //蛇眼睛不同方向的y的偏移量
            [-1, -1],
            [-1, 1],
            [1, 1],
            [1, -1],
        ];
    }

    start() {

    }

    set_direction(d) {
        this.direction = d;
    }

    check_tail_increasing() { //检测当前回合蛇的长度是否增加
        if (this.step <= 10) return true;
        if (this.step % 3 === 1) return true;
        return false;
    }

    next_step() {  // 将蛇的状态变为走下一步
        const d = this.direction;
        this.next_cell = new Cell(this.cells[0].r + this.dr[d], this.cells[0].c + this.dc[d]);
        this.eye_direction = d;
        this.direction = -1;  // 清空操作
        this.status = "move";
        this.step ++ ;

        const k = this.cells.length;
        for (let i = k; i > 0; i -- ) {
            this.cells[i] = JSON.parse(JSON.stringify(this.cells[i - 1]));
        }

        if (!this.gamemap.check_valid(this.next_cell)) { //下一步操作撞了，蛇瞬间去世
            this.status = "die";
        }
    }

    update_move() {
        const dx = this.next_cell.x - this.cells[0].x;
        const dy = this.next_cell.y - this.cells[0].y;
        const distance = Math.sqrt(dx * dx + dy * dy);

        if (distance < this.eps) { //走到目标点了
            this.cells[0] = this.next_cell; //添加一个新蛇头
            this.next_cell = null;
            this.status = "idle"; //走完了，停下来

            if (!this.check_tail_increasing()) { //蛇不变长，砍掉蛇尾
                this.cells.pop();
            }
        } else {
            const move_distance = this.speed * this.timedelta / 1000; // 每两帧之间走的距离
            this.cells[0].x += move_distance * dx / distance;
            this.cells[0].y += move_distance * dy / distance;

            if (!this.check_tail_increasing()) {
                const k = this.cells.length;
                const tail = this.cells[k - 1], tail_target = this.cells[k - 2];
                const tail_dx = tail_target.x - tail.x;
                const tail_dy = tail_target.y - tail.y;
                tail.x += move_distance * tail_dx / distance;
                tail.y += move_distance * tail_dy / distance;
            }
        }
    }

    update() { //每一帧执行一次
        if (this.status === 'move') {
            this.update_move();
        }
        this.render();
    }

    render() {
        const L = this.gamemap.L;
        const ctx = this.gamemap.ctx;
        
        ctx.fillStyle = this.color;
        if (this.status === "die") {
            ctx.fillStyle = "white";
        }

        for (const cell of this.cells) { //of遍历值，in遍历下标
            ctx.beginPath();
            //画圆函数的参数分别是：行坐标，列坐标，半径，起始角度，终止角度
            ctx.arc(cell.x * L, cell.y * L, L / 2 * 0.8, 0, Math.PI * 2);
            ctx.fill();
        }

        for (let i = 1; i < this.cells.length; i++) {
            const a = this.cells[i - 1], b = this.cells[i];
            if (Math.abs(a.x - b.x) < this.eps && Math.abs(a.y - b.y) < this.eps) {
                continue;
            }
            if (Math.abs(a.x - b.x) < this.eps) {
                ctx.fillRect((a.x - 0.4) * L, Math.min(a.y, b.y) * L, L * 0.8, Math.abs(a.y - b.y) * L);
            }
            else {
                ctx.fillRect(Math.min(a.x, b.x) * L, (a.y - 0.4) * L, Math.abs(a.x - b.x) * L, L * 0.8);
            }
        }

        ctx.fillStyle = "black";
        for (let i = 0; i < 2; i++) {
            const eye_x = (this.cells[0].x + this.eye_dx[this.eye_direction][i] * 0.15) * L;
            const eye_y = (this.cells[0].y + this.eye_dy[this.eye_direction][i] * 0.15) * L;
            ctx.beginPath();
            ctx.arc(eye_x, eye_y, L * 0.05, 0, Math.PI * 2);
            ctx.fill();
        }
    }
}
```



```js
export class GameMap extends AcGameObject {
    constructor(ctx, parent) {
        super();

        this.ctx = ctx;
        this.parent = parent;
        this.L = 0;

        this.rows = 13;
        this.cols = 14;
        
        this.inner_walls_count = 20;
        this.walls = [];
        
        // 创建两条蛇
        this.snakes = [
            new Snake({id: 0, color: "#4876EC", r: this.rows - 2, c: 1}, this),
            new Snake({id: 1, color: "#F94848", r: 1, c: this.cols - 2}, this),
        ];
    }
    ...
}
```



目前到20分钟为止

### 在意的内容

- 公共组件
- scoped
- bootstrap
- 导入和使用其他组件
- 页面跳转的实现（router）
- router-link
- setup()
- 获取当前routes
- 聚焦
- class和:class
- slot

---

## 4. 配置Mysql与注册登录模块

### *第1部分代码仓库地址*

https://github.com/NidoSen/KOB/tree/448356fb2ab92ea5b6ba2c266e8d2d1b3e163de7

### 3.1 项目目录结构

先结合讲义把该配的环境（mysql安装+各种依赖+application.properties加内容）都配了

本项目涉及的两张数据表的内容：

- bot（记录每个bot的信息）
  - id（唯一标识，主键，自增）
  - user_id（对应的是user表的id）
  - title
  - description
  - content
  - rating
  - createtime
  - modifytime
- user（记录每个用户的信息）
  - id（唯一标识，主键，自增）
  - username
  - password
  - photo

结合涉及的内容，项目的目录结构如下：

1. controller（controller层，负责请求转发，接受页面过来的参数，传给Service处理，接到返回值，再传给页面）
   1. pk
      - BotInfoController.java
      - IndexController.java
   2. user
      1. account
         - InfoController.java
         - LoginController.java
         - RegisterController.java
      2. bot
         - AddController.java
         - GetListController.java
         - RemoveController.java
         - UpdateController.java
2. mapper（mapper层（也叫Dao层）：将pojo层的class中的操作，映射成sql语句）（借助mybatis-plus实现）
   - BotMapper.java（接口）
   - UserMapper.java（接口）
3. pojo（pojo层：将数据库中的表对应成Java中的Class）
   - Bot.java
   - User.java
4. Service（service层：写具体的业务逻辑，组合使用mapper中的操作）
   1. impl
      1. user
         - account
           - InfoServiceImpl.java
           - LoginServiceImpl.java
           - RegisterServiceImpl.java
         - bot
           - AddServiceImpl.java
           - GetListServiceImpl.java
           - RemoveServiceImpl.java
           - UpdateServiceImpl.java
      2. utils
         - UserDetailsImpl.java
      3. UserDetailsServiceImpl.java
   2. user
      1. account
         - InfoService.java（接口）
         - LoginService.java（接口）
         - RegisterService.java（接口）
      2. bot
         - AddService.java（接口）
         - GetListService.java（接口）
         - RemoveService.java（接口）
         - UpdateService.java（接口）
5. utils
   1. JwtUtil.java

### 3.2 MyBatis-Plus基础使用

- 以mapper层的UserMapper接口为例，首先是mapper层继承BaseMapper接口

  ```java
  package com.kob.backend.mapper;
  
  import com.baomidou.mybatisplus.core.mapper.BaseMapper;
  import com.kob.backend.pojo.User;
  import org.apache.ibatis.annotations.Mapper;
  
  @Mapper
  public interface UserMapper extends BaseMapper<User> {
  
  }
  ```

- 接着在需要用到的地方先注入UserMapper，然后可以使用Mybatis-plus的各种方法，具体可参考官网的 [CURD接口](https://baomidou.com/pages/49cc81/#service-crud-%E6%8E%A5%E5%8F%A3) 和 [条件构造器](https://baomidou.com/pages/10c804/#abstractwrapper)

  ```java
  @RestController
  public class UserController {
  
      @Autowired //自动注入
      UserMapper userMapper;
      
      //查询所有用户
      @GetMapping ("/user/all/")
      public List<User> getAll() {
          return userMapper.selectList(null);
      }
      
      //根据ID查询用户
      @GetMapping ("/user/{userId}/") //userId用大括号包围，加上下一行的注解@PathVariable，URL中的userId就能被使用
      public User getuser(@PathVariable int userId) {
          //写法1（直接查询）
          return userMapper.selectById(userId);
          //写法2（使用条件构造器查询）
          //QueryWrapper<User> queryWrapper = new QueryWrapper<>();
          //queryWrapper.eq("id", userId);
          //return userMapper.selectOne(queryWrapper);
      }
  }
  ```

- 5

### 3.3 Spring Security配置实现登录页面

引入 `spring-boot-starter-security` 依赖后，未登录前无法访问，会直接转到登录界面（默认用户名是user，密码每次启动生成一个）：

<img src="myResources\4.1 登录页面.png" style="zoom:67%;" />

同时还有相应的登出页面：

<img src="myResources\4.2 登出页面.png" style="zoom:67%;" />

登录一次后，之后的一段时间都不需要登录

<img src="myResources\4.3 sessionId和Cookie.png" style="zoom: 50%;" />

原因是登录成功后springboot会传一个sessonid给前端，同时存一份在自己或mysql这里，前端或客户端拿到sessionid后，会存放到cookie里，之后的登录，前端会默认从cookie里取出来sessonid，springboot会比较前端传过来的sessionid和自己这里存的sessionid，如果相同，则登录成功，于是后面几次的登录就绕过了密码（但目前主流的方法是JWT验证（第2部分说明））

目前的登录页面还只能使用默认用户名和生成密码登录，为了能把登录功能和数据库对接，需要完成以下工作：

- 实现一个`service.impl.UserDetailsServiceImpl` 类，继承自 `UserDetailsService` 接口，用来接入数据库信息

  ```java
  ...
  
  @Service
  public class UserDetailsServiceImpl implements UserDetailsService {
  
      @Autowired
      private UserMapper userMapper;
  
      @Override
      public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
          QueryWrapper<User> queryWrapper = new QueryWrapper<>();
          queryWrapper.eq("username", username);
          User user = userMapper.selectOne(queryWrapper);
          if (user == null) {
              throw new RuntimeException("用户不存在");
          }
  
          return new UserDetailsImpl(user); //注意返回的内容是第二步编写的类
      }
  }
  ```

- 实现一个 `service.impl.utils.UserDetailsImpl` 类，继承自 `UserDetails` 接口

  ```java
  ...
  
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public class UserDetailsImpl implements UserDetails {
  
      private User user; //使用User表判断
  
      @Override
      public Collection<? extends GrantedAuthority> getAuthorities() {
          return null;
      }
  
      @Override
      public String getPassword() { //返回密码
          return user.getPassword();
      }
  
      @Override
      public String getUsername() { //返回用户名
          return user.getUsername();
      }
  
      @Override
      public boolean isAccountNonExpired() {
          return true;
      }
  
      @Override
      public boolean isAccountNonLocked() { //账户是否没被锁定
          return true;
      }
  
      @Override
      public boolean isCredentialsNonExpired() { //授权是否还没过期
          return true;
      }
  
      @Override
      public boolean isEnabled() { //是否被启用
          return true;
      }
  }
  ```

- 上面操作完了直接登录可能会出错，原因是到目前进度为止，数据库里存的密码还是明文，而 `spring-boot-starter-security` 需要使用使用密文登录，且目前还没有实现加密类 `PasswordEncoder` ，因此会报错，如果想使用明文直接调试，对应的数据库存储的明文密码前面需要加上 `{noop}`

- 实现 `config.SecurityConfig` 类，用来实现用户密码的加密存储（同时数据库也需要存储密文密码，而不是明文密码）

  ```java
  ...
  
  @Configuration
  @EnableWebSecurity
  public class SecurityConfig {
  
      @Bean
      public PasswordEncoder passwordEncoder() {
          return new BCryptPasswordEncoder();
      }
  }
  ```

- 上一步完成后，新增用户的功能也要相应改写，实现密文密码存储

  ```java
  ...
  
  @RestController
  public class UserController {
  
      @Autowired
      UserMapper userMapper;
  
      ...
  
      @GetMapping ("/user/add/{userId}/{username}/{password}/")
      public String addUser(
              @PathVariable int userId,
              @PathVariable String username,
              @PathVariable String password) {
          if (password.length() < 6) {
              return "密码太短";
          }
          PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(); //获取加密工具
          String encondedPassword = passwordEncoder.encode(password); //对明文密码加密得到密文密码
          //存储密文密码
          User user = new User(userId, username, encondedPassword);
          userMapper.insert(user);
          return "Add User Successfully!";
      }
  
      ...
  }
  ```

### *第2部分代码仓库地址*

https://github.com/NidoSen/KOB/tree/2ca42bef50ac5675471ad2dcb13b445098655244

### 3.4 JWT令牌

上节课的登录方式的缺陷：跨域比较难处理

更合适的方式是使用JWT令牌验证

<img src="myResources\4.4 JWT.png" style="zoom: 67%;" />

具体实现：

- 按照讲义引入3个和JWT相关的依赖

- 实现 `utils.JwtUtil` 类，为 `jwt` 工具类，用来创建、解析 `jwt token`

  ```java
  ...
  
  @Component
  public class JwtUtil { // 第一个作用是将字符串加上秘钥和有效期变成加密后的字符串；第二个作用是给令牌，将userId解析出来
      public static final long JWT_TTL = 60 * 60 * 1000L * 24 * 14;  // 有效期14天
      public static final String JWT_KEY = "SDFGjhdsfalshdfHFdsjkdsfds121232131afasdfac";
  
      public static String getUUID() {
          return UUID.randomUUID().toString().replaceAll("-", "");
      }
  
      public static String createJWT(String subject) {
          JwtBuilder builder = getJwtBuilder(subject, null, getUUID());
          return builder.compact();
      }
  
      private static JwtBuilder getJwtBuilder(String subject, Long ttlMillis, String uuid) {
          SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
          SecretKey secretKey = generalKey();
          long nowMillis = System.currentTimeMillis();
          Date now = new Date(nowMillis);
          if (ttlMillis == null) {
              ttlMillis = JwtUtil.JWT_TTL;
          }
  
          long expMillis = nowMillis + ttlMillis;
          Date expDate = new Date(expMillis);
          return Jwts.builder()
                  .setId(uuid)
                  .setSubject(subject)
                  .setIssuer("sg")
                  .setIssuedAt(now)
                  .signWith(signatureAlgorithm, secretKey)
                  .setExpiration(expDate);
      }
  
      public static SecretKey generalKey() {
          byte[] encodeKey = Base64.getDecoder().decode(JwtUtil.JWT_KEY);
          return new SecretKeySpec(encodeKey, 0, encodeKey.length, "HmacSHA256");
      }
  
      public static Claims parseJWT(String jwt) throws Exception {
          SecretKey secretKey = generalKey();
          return Jwts.parserBuilder()
                  .setSigningKey(secretKey)
                  .build()
                  .parseClaimsJws(jwt)
                  .getBody();
      }
  }
  ```

  - 新版的 `jjwt-api` 中的 `JwtParserBuilder` 变了，需要进行调整

    ```java
    return Jwts.builder()
            .id(uuid)
            .subject(subject)
            .issuer("sg")
            .issuedAt(now)
            .signWith(secretKey)
            .expiration(expDate);
    
    return Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(jwt)
            .getPayload();
    ```

- 实现 `config.filter.JwtAuthenticationTokenFilter` 类，用来验证 `jwt token` ，如果验证成功，则将 `User` 信息注入上下文中

  ```java
  package com.kob.backend.config.filter;
  
  import com.kob.backend.mapper.UserMapper;
  import com.kob.backend.pojo.User;
  import com.kob.backend.service.impl.utils.UserDetailsImpl;
  import com.kob.backend.utils.JwtUtil;
  import io.jsonwebtoken.Claims;
  import org.jetbrains.annotations.NotNull;
  import org.springframework.beans.factory.annotation.Autowired;
  import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
  import org.springframework.security.core.context.SecurityContextHolder;
  import org.springframework.stereotype.Component;
  import org.springframework.util.StringUtils;
  import org.springframework.web.filter.OncePerRequestFilter;
  
  import javax.servlet.FilterChain;
  import javax.servlet.ServletException;
  import javax.servlet.http.HttpServletRequest;
  import javax.servlet.http.HttpServletResponse;
  import java.io.IOException;
  
  @Component
  public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {
      @Autowired
      private UserMapper userMapper;
  
      @Override
      protected void doFilterInternal(HttpServletRequest request, @NotNull HttpServletResponse response, @NotNull FilterChain filterChain) throws ServletException, IOException {
          String token = request.getHeader("Authorization");
  
          if (!StringUtils.hasText(token) || !token.startsWith("Bearer ")) {
              filterChain.doFilter(request, response);
              return;
          }
  
          token = token.substring(7);
  
          String userid;
          try {
              Claims claims = JwtUtil.parseJWT(token);
              userid = claims.getSubject();
          } catch (Exception e) {
              throw new RuntimeException(e);
          }
  
          User user = userMapper.selectById(Integer.parseInt(userid));
  
          if (user == null) {
              throw new RuntimeException("用户名未登录");
          }
  
          UserDetailsImpl loginUser = new UserDetailsImpl(user);
          UsernamePasswordAuthenticationToken authenticationToken =
                  new UsernamePasswordAuthenticationToken(loginUser, null, null);
  
          SecurityContextHolder.getContext().setAuthentication(authenticationToken);
  
          filterChain.doFilter(request, response);
      }
  }
  
  ```

  - `import org.jetbrains.annotations.NotNull;` 爆红要加上 `JetBrains Java Annotations` 这个依赖

  - 高版本springboot可能需要修改

    ```java
    import jakarta.servlet.FilterChain;
    import jakarta.servlet.ServletException;
    import jakarta.servlet.http.HttpServletRequest;
    import jakarta.servlet.http.HttpServletResponse;
    ```

- 配置 `config.SecurityConfig` 类，放行登录、注册等接口

  ```java
  ...
  
  @Configuration
  @EnableWebSecurity
  @ComponentScan(basePackages = {"com.kob.backend.config"})
  public class SecurityConfig extends WebSecurityConfigurerAdapter {
      @Autowired
      private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;
  
      @Bean
      public PasswordEncoder passwordEncoder() {
          return new BCryptPasswordEncoder();
      }
  
      @Bean
      @Override
      public AuthenticationManager authenticationManagerBean() throws Exception {
          return super.authenticationManagerBean();
      }
  
      @Override
      protected void configure(HttpSecurity http) throws Exception {
          http.csrf().disable()
                  .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                  .and()
                  .authorizeRequests()
                  .antMatchers("/user/account/token/", "/user/account/register/").permitAll()
                  .antMatchers(HttpMethod.OPTIONS).permitAll()
                  .anyRequest().authenticated();
  
          http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
      }
  }
  ```

### 3.5 API编写

首先User表需要实现唯一标识，即自增的主键，因此：

- 数据库修改User表属性实现自增

  <img src="myResources\4.5 自增User Id.png" style="zoom:67%;" />

- User类需要一同修改

  ```java
  @Data
  @NoArgsConstructor
  @AllArgsConstructor
  public class User {
      @TableId(type = IdType.AUTO) //实现自增
      private Integer id; //不使用int避免Mybait-plus出错
      private String username;
      private String password;
      private String photo; //新增的phote，存头像
  }
  ```

实现 `/user/account/token/`：验证用户名密码，验证成功后返回 `jwt token`（令牌）：

- service/user/account/LoginService.java

  ```java
  ...
  
  public interface LoginService {
      public Map<String, String> getToken(String username, String password);
  }

- service/impl/user/account/LoginServiceImpl.java

  ```java
  ...
  
  @Service
  public class LoginServiceImpl implements LoginService {
  
      @Autowired
      private AuthenticationManager authenticationManager; //一个固定需要的工具，用于验证用户是否登录成功
  
      @Override
      //这个函数实现的是根据前端传过来的用户名和密码，完成检验和生成JWT-token的功能
      public Map<String, String> getToken(String username, String password) {
          //将明文密码加密成密文密码，并和用户名一同封装
          UsernamePasswordAuthenticationToken authenticationToken =
                  new UsernamePasswordAuthenticationToken(username, password);
          
          //验证是否能够登陆
          Authentication authenticate = authenticationManager.authenticate(authenticationToken); //登陆失败，会报异常，自动处理
          
          //登录成功了，可以取出用户并生成JWT-token
          UserDetailsImpl loginUser = (UserDetailsImpl) authenticate.getPrincipal();
          User user = loginUser.getUser();
          String jwt = JwtUtil.createJWT(user.getId().toString());
          
          //封装返回给前端的数据，有一定格式要求
          Map<String, String> map = new HashMap<>();
          map.put("error_message", "success");
          map.put("token", jwt);
  
          return map;
      }
  }
  ```

- controller/user/account/LoginController.java

  ```java
  ...
  
  @RestController
  public class LoginController {
      @Autowired
      private LoginService loginService;
  
      @PostMapping("/user/account/token/")
      public Map<String, String> getToken(@RequestParam Map<String, String> map) {
          //取出前端传过来的用户名和密码
          String username = map.get("username");
          String password = map.get("password");
          
          //得到JWT-token（同时检查用户名和密码是否正确）
          return loginService.getToken(username, password);
      }
  
  }
  ```

- 调试可以直接在前端选一个页面（如App.vue）用ajax实现

  ```vue
  ...
  
  <script>
  import NavBar from "./components/NavBar.vue";
  import "bootstrap/dist/css/bootstrap.min.css";
  import "bootstrap/dist/js/bootstrap";
  import $ from 'jquery' //导入jquery
  
  export default {
    components: {
      NavBar,
    },
    setup() {
      $.ajax({
        url: "http://127.0.0.1:3000/user/account/token/",
        type: "post",
        data: {
          username: 'b',
          password: 'pb',
        },
        success(resp) {
          console.log(resp);
        },
        error(resp) {
          console.log(resp);
        }
      })
    }
  };
  </script>
  
  ...
  ```
  

实现 `/user/account/info/`：根据令牌返回用户信息

- service/user/account/InfoService.java

  ```java
  ...
  
  public interface InfoService {
      public Map<String, String> getinfo();
  }
  ```

- service/impl/user/account/InfoServiceImpl.java

  ```java
  ...
  
  @Service
  public class InfoServiceImpl implements InfoService {
      @Override
      public Map<String, String> getinfo() {
          //从上下文将JWT-token提取出来
          UsernamePasswordAuthenticationToken authenticationToken =
                  (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
          
          //从JWT-token中得到登录用户
          UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
          User user = loginUser.getUser();
          
          //生成返回结果
          Map<String, String> map = new HashMap<>();
          map.put("error_message", "success");
          map.put("id", user.getId().toString());
          map.put("username", user.getUsername());
          map.put("password", user.getPassword());
          map.put("photo", user.getPhoto());
  
          return map;
      }
  }
  ```

- controller/user/account/InfoController.java

  ```java
  ...
  
  @RestController
  public class InfoController {
      @Autowired
      private InfoService infoService;
  
      @GetMapping("user/account/info/")
      public Map<String, String> getinfo() {
          return infoService.getinfo();
      }
  }

- 调试可以直接在前端选一个页面（如App.vue）用ajax实现

  ```vue
  ...
  
  <script>
  import NavBar from "./components/NavBar.vue";
  import "bootstrap/dist/css/bootstrap.min.css";
  import "bootstrap/dist/js/bootstrap";
  import $ from 'jquery' //导入jquery
  
  export default {
    components: {
      NavBar,
    },
     setup() {
       $.ajax({
        url: "http://127.0.0.1:3000/user/account/info/",
        type: "get",
        //传一个表头
        headers: {
          Authorization: "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJlOWI5OTQwMTUxNGM0M2ZmOTE0MzUwMDM5MGUwYmU1MSIsInN1YiI6IjIiLCJpc3MiOiJzZyIsImlhdCI6MTY5NTUzNTA3MCwiZXhwIjoxNjk2NzQ0NjcwfQ.NWvk4eHAgrm7PLXHAj2fFzKngutTyQYD2Xrkks1RtY0", // 注意Bearer后面有一个空格
        },
        success(resp) {
          console.log(resp);
        },
        error(resp) {
          console.log(resp);
        }
      })
    }
  };
  </script>
  
  ...
  ```

实现 `/user/account/register/`：注册账号

- service/user/account/RegisterService.java

  ```java
  ...
  
  public interface RegisterService {
      public Map<String, String> register(String username, String password, String confirmedPassword);
  }

- service/impl/user/account/RegisterServiceImpl.java

  ```java
  ...
  
  @Service
  public class RegisterServiceImpl implements RegisterService {
      @Autowired
      private UserMapper userMapper;
  
      @Autowired
      private PasswordEncoder passwordEncoder;
  
      @Override
      public Map<String, String> register(String username, String password, String confirmedPassword) {
          Map<String, String> map = new HashMap<>();
          if (username == null) {
              map.put("error_message", "用户名不能为空");
              return map;
          }
          if (password == null || confirmedPassword == null) {
              map.put("error_message", "密码不能为空");
              return map;
          }
  
          username = username.trim();
          if (username == null) {
              map.put("error_message", "用户名不能为空");
              return map;
          }
  
          if (password.length() == 0 || confirmedPassword.length() == 0) {
              map.put("error_message", "密码不能为空");
              return map;
          }
  
          if (username.length() > 100) {
              map.put("error_message", "用户名长度不能大于100");
              return map;
          }
  
          if (password.length() > 100 || confirmedPassword.length() > 100) {
              map.put("error_message", "密码长度不能大于100");
              return map;
          }
  
          if (!password.equals(confirmedPassword)) {
              map.put("error_message", "两次输入的密码不一致");
              return map;
          }
  
          QueryWrapper<User> queryWrapper = new QueryWrapper<>();
          queryWrapper.eq("username", username);
          List<User> users = userMapper.selectList(queryWrapper);
          if (!users.isEmpty()) {
              map.put("error_message", "用户名已存在");
              return map;
          }
  
          String encodedPassword = passwordEncoder.encode(password);
          String photo = "https://www.acwing.com/user/profile/index/";
          User user = new User(null, username, encodedPassword, photo);
          userMapper.insert(user);
  
          map.put("error_message", "success");
          return map;
      }
  }
  ```

- controller/user/account/RegisterController.java

  ```java
  ...
  
  @RestController
  public class RegisterController {
      @Autowired
      private RegisterService registerService;
  
      @PostMapping("/user/account/register/")
      public Map<String, String> register(@RequestParam Map<String, String> map) {
          String username = map.get("username");
          String password = map.get("password");
          String confirmedPassword = map.get("confirmedPassword");
          return registerService.register(username, password, confirmedPassword);
      }
  }

- 调试可以直接在前端选一个页面（如App.vue）用ajax实现

  ```vue
  ...
  
  <script>
  import NavBar from "./components/NavBar.vue";
  import "bootstrap/dist/css/bootstrap.min.css";
  import "bootstrap/dist/js/bootstrap";
  import $ from 'jquery' //导入jquery
  
  export default {
    components: {
      NavBar,
    },
     setup() {
       $.ajax({
        url: "http://127.0.0.1:3000/user/account/register/",
        type: "post",
        data: {
          username: "yxc",
          password: "123",
          confirmedPassword: "123",
        },
        success(resp) {
          console.log(resp);
        },
        error(resp) {
          console.log(resp);
        }
      })
    }
  };
  </script>
  
  ...
  ```

### 3.6 前端实现登录页面

登录页面（顺便把注册页面的前两步也完成了）：

- 第一步，创建views/user/account/UserAccountLoginView.vue和/views/user/account/UserAccountRegisterView.vue，先从views/ranklist/RanklistIndexView.vue将页面复制过来，进行调整

  ```vue
  <template>
      <ContentField>
          登录
      </ContentField>
  </template>
  
  <script>
  import ContentField from '../../../components/ContentField.vue' //根据目录结构，这里多一个../
  
  export default {
      components: {
          ContentField
      }
  }
  </script>
  
  <style scoped>
  </style>
  ```

  ```vue
  <template>
      <ContentField>
          注册
      </ContentField>
  </template>
  
  <script>
  import ContentField from '../../../components/ContentField.vue'
  
  export default {
      components: {
          ContentField
      }
  }
  </script>
  
  <style scoped></style>

- 第二步，改写web/src/router/index.js，将登录页面和注册页面的URL加到路由里

  ```js
  import { createRouter, createWebHistory } from 'vue-router'
  ...
  import UserAccountLoginView from '../views/user/account/UserAccountLoginView.vue'
  import UserAccountRegisterView from '../views/user/account/UserAccountRegisterView.vue'
  
  
  const routes = [
    ...
    {
      path: "/user/account/login/",
      name: "user_account_login",
      component: UserAccountLoginView,
    },
    {
      path: "/user/account/register/",
      name: "user_account_register",
      component: UserAccountRegisterView,
    },
    ...
  ]
  
  const router = createRouter({
    history: createWebHistory(),
    routes
  })
  
  export default router

- 第三步，修改views/user/account/UserAccountLoginView.vue，创建store/user.js，修改store/index.js

  views/user/account/UserAccountLoginView.vue，这一步用到很多vue3的基本概念，需要看vue3的[讲义](https://www.acwing.com/blog/content/20725/)

  ```vue
  <template>
      <ContentField>
          <!-- div.row>div.col-3>div + tab键，快速创建bootstrap中的一个grid布局 -->
          <div class="row justify-content-md-center"><!-- 居中 -->
              <div class="col-3">
                  <!-- 加入bootstrap的一个表单（form）样式 -->
                  <form @submit.prevent="login"><!-- 如果点解了按钮，就触发login函数，.prevent是阻止默认行为，即阻止组件之间的向上或向下传递 -->
                      <div class="mb-3">
                          <label for="username" class="form-label">用户名</label>
                          <!-- v-model="username"实现表单textarea中的数据和js部分的username双向绑定，placeholder为输入提示-->
                          <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                      </div>
                      <div class="mb-3">
                          <label for="password" class="form-label">密码</label>
                          <!-- type="password"能将密码显示为加密的*，placeholder为输入提示-->
                          <input v-model="password" type="password" class="form-control" id="password" placeholder="请输入密码">
                      </div>
                      <!-- 显示报错信息 -->
                      <div class="error-message">{{ error_message }}</div>
                      <button type="submit" class="btn btn-primary">提交</button>
                  </form>
              </div>
          </div>
      </ContentField>
  </template>
  
  <script>
  import ContentField from '../../../components/ContentField.vue'
  import { useStore } from 'vuex' //要用到vuex中的全局变量，因此需要先将useStore导入
  import { ref } from 'vue'
  import router from '../../../router/index.js' //因为要实现登录后跳转页面，所以需要导入router使用路由
  
  export default {
      components: {
          ContentField
      },
      setup() {
          const store = useStore();
          let username = ref('');
          let password = ref('');
          let error_message = ref('');
  
          const login = () => {
              error_message = "";
              store.dispatch("login", { //调用store/user.js中的login函数
                  //下面两行的第一个username和password是传给store/user.js中的login函数的data参数，而第二个username和password是上面用ref定义的响应式变量
                  username: username.value,
                  password: password.value,
                  //store.dispatch调用的时候会定义success函数和error函数
                  success() {
                      //成功之后先获取用户的信息
                      store.dispatch("getinfo", {
                          success() {
                              router.push({ name: 'home' }); //跳转到pk页面
                              console.log(store.state.user)
                          }
                      })
                  },
                  error() {
                      error_message.value = "用户名或密码错误";
                  }
              })
          }
  
          return {
              username,
              password,
              error_message,
              login,
          }
      }
  }
  </script>
  
  <style scoped>
  button {
      width: 100%;
  }
  
  div.error-message {
      color: red;
  }
  </style>
  ```

  store/user.js

  ```js
  import $ from 'jquery'
  
  //这里是user子模块
  export default {
      //state: 存储所有数据，可以用modules属性划分成若干模块
      state: {
          id: "",
          username: "",
          photo: "",
          token: "",
          is_login: false,
      },
      //getters：根据state中的值计算新的值
      getters: {
      },
      //mutations：所有对state的修改操作都需要定义在这里，不支持异步，可以通过$store.commit()触发
      mutations: {
          updateUser(state, user) {
              state.id = user.id;
              state.username = user.username;
              state.photo = user.photo;
              state.is_login = user.is_login;
          },
          updateToken(state, token) {
              state.token = token;
          },
          logout(state) {
              state.id = "";
              state.username = "";
              state.photo = "";
              state.token = "";
              state.is_login = false;
          }
      },
      //actions：定义对state的复杂修改操作，支持异步，可以通过$store.dispatch()触发。注意不能直接修改state，只能通过mutations修改state
      actions: {
          login(context, data) {
              $.ajax({
                  url: "http://127.0.0.1:3000/user/account/token/",
                  type: "post",
                  data: {
                      username: data.username,
                      password: data.password,
                  },
                  success(resp) {
                      if (resp.error_message === "success") { //返回的error_message是"success"才是真的成功了
                          //actions不能直接修改state，需要通过$store.commit()（不过这里是context.commit，context参数也是这么用的）触发mutations中的函数实现对state的修改
                          context.commit("updateToken", resp.token);
                          data.success(resp); //store.dispatch调用的时候会定义success函数和error函数
                      }
                      else {
                          data.error(resp);
                      }
                  },
                  error(resp) {
                      data.error(resp);
                  }
              });
          },
          getinfo(context, data) {
              $.ajax({
                  url: "http://127.0.0.1:3000/user/account/info/",
                  type: "get",
                  headers: {
                      Authorization: "Bearer " + context.state.token, //注意Bearer后面的空格别忘了
                  },
                  success(resp) {
                      if (resp.error_message === "success") { //返回的error_message是"success"才是真的成功了
                          context.commit("updateUser", {
                              ...resp, //将resp解析出来
                              is_login: true,
                          })
                          data.success(resp);
                      }
                  },
                  error(resp) {
                      data.error(resp);
                  }
              })
          },
          logout(context) {
              context.commit("logout");
              location.reload(); //退出登录后刷新页面
          }
      },
      //modules：定义state的子模块
      modules: {
      }
  }
  ```

  store/index.js

  ```js
  //vuex：存储全局状态，全局唯一
  import { createStore } from 'vuex'
  import ModuleUser from './user' //将user子模块导入全局信息state存储
  
  export default createStore({
    state: {
    },
    getters: {
    },
    mutations: {
    },
    actions: {
    },
    modules: {
      user: ModuleUser, //state的user子模块
    }
  })

- 第四步，修改导航栏components/NavBar.vue，未登录右上角显示“登录/注册”，登录了右上角显示用户名

  ```vue
  <template>
    <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
      <div class="container">
        <router-link class="navbar-brand" :to="{ name: 'home' }">King of Bots</router-link>
        <div class="collapse navbar-collapse" id="navbarText">
          ...
          
          <!-- 在html里使用store，前面要加$ -->
          <ul class="navbar-nav" v-if="$store.state.user.is_login"> <!-- 如果登陆了，显示用户名 -->
            <li class="nav-item dropdown">
              <a class="nav-link dropdown-toggle" href="#" role="button" data-bs-toggle="dropdown" aria-expanded="false">
                {{ $store.state.user.username }}
              </a>
              <ul class="dropdown-menu">
                <li>
                  <router-link class="dropdown-item" :to="{ name: 'user_bot_index' }">我的Bot</router-link>
                </li>
                <li>
                  <hr class="dropdown-divider">
                </li>
                <!-- 退出登录 -->
                <li><a class="dropdown-item" href="#" @click="logout">退出</a></li>
              </ul>
            </li>
          </ul>
          <ul class="navbar-nav" v-else> <!-- 如果没登录，显示登录和注册按钮 -->
            <li class="nav-item dropdown">
              <!-- 点击登录，跳转到登录页面 -->
              <router-link class="nav-link" :to="{ name: 'user_account_login' }" role="button">
                登陆
              </router-link>
            </li>
            <li class="nav-item dropdown">
              <!-- 点击注册，跳转到注册页面 -->
              <router-link class="nav-link" :to="{ name: 'user_account_register' }" role="button">
                注册
              </router-link>
            </li>
          </ul>
        </div>
      </div>
    </nav>
  </template>
  
  <script>
  import { useRoute } from 'vue-router'
  import { computed } from 'vue'
  import { useStore } from 'vuex' //有两个地方需要用的store中的数据和函数，一是显示在右上角的用户名，二是下拉菜单的登出按钮
  
  export default {
    setup() {
      const store = useStore();
      const route = useRoute();
      let route_name = computed(() => route.name)
  
      const logout = () => {
        store.dispatch("logout");
      }
      return {
        route_name,
        logout,
      }
    }
  }
  </script>
  
  <style scoped></style>
  ```

### *第3部分代码仓库地址*

https://github.com/NidoSen/KOB/tree/f7d6b62924651a097d21783f59e2b0416a0984cb

### 3.7 前端页面授权

实现未登录时直接跳转到登录页面

修改router/index.js

```js
...

import store from '../store/index.js'

const routes = [
  {
    path: "/",
    name: "home",
    redirect: "/pk/",
    meta: { // 需要授权才能进入的页面（也就是beforeEach不需要跳转）
      requestAuth: true,
    }
  },
  {
    path: "/pk/",
    name: "pk_index",
    component: PkIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/record/",
    name: "record_index",
    component: RecordIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/ranklist/",
    name: "ranklist_index",
    component: RanklistIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/user/bot",
    name: "user_bot_index",
    component: UserBotIndexView,
    meta: {
      requestAuth: true,
    }
  },
  {
    path: "/user/account/login/",
    name: "user_account_login",
    component: UserAccountLoginView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/user/account/register/",
    name: "user_account_register",
    component: UserAccountRegisterView,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/404/",
    name: "not_found",
    component: NotFound,
    meta: {
      requestAuth: false,
    }
  },
  {
    path: "/:catchAll()",
    redirect: "/404/"
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => { //使用router进入每个页面前会先调用这个函数
  if (to.meta.requestAuth && !store.state.user.is_login) { //如果去的页面是授权页面且当前未登录，需要重定向到登录页面
    next({ name: "user_account_login" });
  }
  else {
    next();
  }
})

export default router
```

### 3.8 前端实现注册页面

和登录页面很像

```vue
<template>
    <ContentField>
        <div class="row justify-content-md-center">
            <div class="col-3">
                <form @submit.prevent="register">
                    <div class="mb-3">
                        <label for="username" class="form-label">用户名</label>
                        <input v-model="username" type="text" class="form-control" id="username" placeholder="请输入用户名">
                    </div>
                    <div class="mb-3">
                        <label for="password" class="form-label">密码</label>
                        <input v-model="password" type="text" class="form-control" id="password" placeholder="请输入密码">
                    </div>
                    <div class="mb-3">
                        <label for="confirmedPassword" class="form-label">确认密码</label>
                        <input v-model="confirmedPassword" type="text" class="form-control" id="confirmedPassword"
                            placeholder="请再次输入密码">
                    </div>
                    <div class="error-message">{{ error_message }}</div>
                    <button type="submit" class="btn btn-primary">提交</button>
                </form>
            </div>
        </div>
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue'
import { ref } from 'vue'
import router from '../../../router/index.js'
import $ from 'jquery'

export default { //这里插一句，如果不加default，则其他组件import的时候需要加大括号，否则不用，且可以任意重命名
    components: {
        ContentField
    },
    setup() {
        let username = ref('');
        let password = ref('');
        let confirmedPassword = ref('');
        let error_message = ref('');
        
        //注册的ajax直接放到这里而不像登录界面那样放到vuex的原因：因为不会修改state的值
        const register = () => {
            $.ajax({
                url: "http://127.0.0.1:3000/user/account/register/",
                type: "post",
                data: {
                    username: username.value,
                    password: password.value,
                    confirmedPassword: confirmedPassword.value,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        router.push({ name: "user_account_login" });
                    }
                    else {
                        error_message.value = resp.error_message;
                    }
                },
            });
        }

        return {
            username,
            password,
            confirmedPassword,
            error_message,
            register
        }
    }
}
</script>

<style scoped>
button {
    width: 100%;
}

div.error-message {
    color: red;
}
</style>
```

### 3.9 登录持久化

目前因为token是存在浏览器内存里，所以关闭浏览器再打开需要重新登陆，如果想浏览器关闭了重新打开还能自动登录，则可以使用localStorage将token存到浏览器的一小部分硬盘空间里

需要修改store/user.js，views/user/account/UserAccountLoginView.vue和components/NavBar.vue

```js
import $ from 'jquery'

export default {
    state: {
        id: "",
        username: "",
        photo: "",
        token: "",
        is_login: false,
        pulling_info: true, //是否正在拉取信息，避免出现闪一下的情况
    },
    ...
    mutations: {
        ...
        updatePullingInfo(state, pulling_info) {
            state.pulling_info = pulling_info;
        }
    },
    actions: {
        login(context, data) {
            $.ajax({
                url: "http://127.0.0.1:3000/user/account/token/",
                type: "post",
                data: {
                    username: data.username,
                    password: data.password,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        localStorage.setItem("jwt_token", resp.token); //将token存到浏览器的硬盘空间里实现持久化
                        context.commit("updateToken", resp.token);
                        data.success(resp);
                    }
                    else {
                        data.error(resp);
                    }
                },
                error(resp) {
                    data.error(resp);
                }
            });
        },
        ...
        logout(context) {
            localStorage.removeItem("jwt_token"); //删除浏览器硬盘空间的token
            context.commit("logout");
        }
    },
    modules: {
    }
}
```

```vue
<template>
    <ContentField v-if="!$store.state.user.pulling_info"><!-- 如果不在拉取jwt_token中，才显示登录页面的内容 -->
        ...
    </ContentField>
</template>

<script>
import ContentField from '../../../components/ContentField.vue'
import { useStore } from 'vuex'
import { ref } from 'vue'
import router from '../../../router/index.js'

export default {
    components: {
        ContentField
    },
    setup() {
        const store = useStore();
        let username = ref('');
        let password = ref('');
        let error_message = ref('');

        const jwt_token = localStorage.getItem("jwt_token");

        if (jwt_token) { //如果jwt_token存在
            store.commit("updateToken", jwt_token); //更新state里的token
            store.dispatch("getinfo", { //如果能获取到用户信息，说明jwt_token是有效的，跳转到首页
                success() {
                    router.push({ name: "home" });
                    store.commit("updatePullingInfo", false);
                },
                error() {
                    store.commit("updatePullingInfo", false);
                }
            })
        }
        else {
            store.commit("updatePullingInfo", false);
        }

        ...
    }
}

...
```

```vue
<template>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <div class="container">
      <router-link class="navbar-brand" :to="{ name: 'home' }">King of Bots</router-link>
      <div class="collapse navbar-collapse" id="navbarText">
        ...

        <ul class="navbar-nav" v-if="$store.state.user.is_login">
          ...
        </ul>
        <!-- 如果不在拉取jwt_token中，才显示导航栏的内容 -->
        <ul class="navbar-nav" v-else-if="!$store.state.user.pulling_info">
          ...
        </ul>
      </div>
    </div>
  </nav>
</template>

...
```

## 5. 配置个人中心页面

### *第1部分代码仓库地址*

https://github.com/NidoSen/KOB/tree/6f7b76315626e7ab6791aeaefb3ec133495cf770

### 5.1 个人中心页面设计和bot表格设计

<img src="myResources\5.1 个人中心页面设计.png" style="zoom:50%;" />

左边是头像，右边最上面的右边点击创建按钮能新建bot，下面是所有bot的列表，每个条目右侧有修改和删除按钮

bot表格设计如下：

```mysql
CREATE TABLE `kob`.`bot`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `title` varchar(100) NULL,
  `description` varchar(300) NULL,
  `content` varchar(10000) NULL,
  `rating` int NULL DEFAULT 1500,
  `createtime` datetime NULL,
  `modifytime` datetime NULL,
  PRIMARY KEY (`id`)
);
```

同时需要修改 pojo/Bot.java，实现自增和日期格式的调整，同时对于各个变量，数据库使用下划线命名法（如 `user_id`），类使用驼峰命名法（如 `userId`），但是注意：在pojo中需要定义成 `userId`，在 `queryWrapper` 中的名称仍然为 `user_id`

```java
...

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bot {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String title;
    private String description;
    private String content;
    private Integer rating;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createtime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date modifytime;
}
```

### 5.2 后端API实现

首先需要创建mapper层 `mapper/BotMapper.java`

```java
...

@Mapper
public interface BotMapper extends BaseMapper<Bot> {

}
```

`/user/bot/add/`：创建一个 `Bot`

- 创建 service/user/bot/AddService.java

  ```java
  ...
  
  public interface AddService {
      Map<String, String> add(Map<String, String> data);
  }

- 创建 service/impl/user/bot/AddServiceImpl.java

  ```java
  ...
  
  @Service
  public class AddServiceImpl implements AddService {
  
      @Autowired
      private BotMapper botMapper;
  
      @Override
      public Map<String, String> add(Map<String, String> data) {
          //类似InfoService，从上下文将JWT-token提取出来，并得到登录用户
          UsernamePasswordAuthenticationToken authenticationToken =
                  (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
          UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
          User user = loginUser.getUser();
  
          String title = data.get("title");
          String description = data.get("description");
          String content = data.get("content");
  
          Map<String, String> map = new HashMap<>();
  
          if (title == null || title.length() == 0) {
              map.put("error_message", "标题不能为空");
              return map;
          }
  
          if (title.length() > 100) {
              map.put("error_message", "标题的长度不能超过100");
              return map;
          }
  
          if (description == null || description.length() == 0) {
              description = "这个用户很懒，什么都没留下~";
          }
  
          if (description.length() > 300) {
              map.put("error_message", "Bot描述的长度不能超过300");
              return map;
          }
  
          if (content == null || content.length() == 0) {
              map.put("error_message", "代码不能为空");
              return map;
          }
  
          if (content.length() > 10000) {
              map.put("error_message", "代码长度不能超过10000");
              return map;
          }
  
          Date now = new Date();
          Bot bot = new Bot(null, user.getId(), title, description, content, 1500, now, now);
  
          botMapper.insert(bot);
          map.put("error_message", "success");
  
          return map;
      }
  }

- 创建 `controller/user/bot/AddController.java`

  ```java
  ...
  
  @RestController
  public class AddController {
      @Autowired
      private AddService addService;
  
      @PostMapping("/user/bot/add/")
      public Map<String, String> add(@RequestParam Map<String, String> data) {
          return addService.add(data);
      }
  }
  ```

`/user/bot/remove/`：删除一个 `Bot`

- 创建 service/user/bot/RemoveService.java

  ```java
  ...
  
  public interface RemoveService {
      Map<String, String> remove(Map<String, String> data);
  }

- 创建 service/impl/user/bot/RemoveSeviceImpl.java

  ```java
  ...
  
  @Service
  public class RemoveSeviceImpl implements RemoveService {
      @Autowired
      private BotMapper botMapper;
  
      @Override
      public Map<String, String> remove(Map<String, String> data) {
          UsernamePasswordAuthenticationToken authenticationToken =
                  (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
          UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
          User user = loginUser.getUser();
  
          int bot_id = Integer.parseInt(data.get("bot_id"));
          Bot bot = botMapper.selectById(bot_id);
          Map<String, String> map = new HashMap<>();
  
          if (bot == null) {
              map.put("error_message", "Bot不存在或已被删除");
              return map;
          }
  
          if (!bot.getUserId().equals(user.getId())) {
              map.put("error_message", "没有权限删除该Bot");
              return map;
          }
  
          botMapper.deleteById(bot_id);
          map.put("error_message", "success");
  
          return map;
      }
  }

- 创建 controller/user/bot/RemoveController.java

  ```java
  ...
  
  @RestController
  public class RemoveController {
      @Autowired
      private RemoveService removeService;
  
      @PostMapping("/user/bot/remove/")
      public Map<String, String> remove(@RequestParam Map<String, String> data) {
          return removeService.remove(data);
      }
  }

`/user/bot/update/`：修改一个 `Bot`

- 创建 service/user/bot/UpdateService.java

  ```java
  ...
  
  public interface UpdateService {
      Map<String, String> update(Map<String, String> data);
  }
  ```

- 创建 service/impl/user/bot/UpdateServiceImpl.java

  ```java
  ...
  
  @Service
  public class UpdateServiceImpl implements UpdateService {
      @Autowired
      private BotMapper botMapper;
  
      @Override
      public Map<String, String> update(Map<String, String> data) {
          UsernamePasswordAuthenticationToken authenticationToken =
                  (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
          UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
          User user = loginUser.getUser();
  
          int bot_id = Integer.parseInt(data.get("bot_id"));
  
          String title = data.get("title");
          String description = data.get("description");
          String content = data.get("content");
  
          Map<String, String> map = new HashMap<>();
  
          if (title == null || title.length() == 0) {
              map.put("error_message", "标题不能为空");
              return map;
          }
  
          if (title.length() > 100) {
              map.put("error_message", "标题长度不能大于100");
              return map;
          }
  
          if (description == null || description.length() == 0) {
              description = "这个用户很懒，什么也没留下~";
          }
  
          if (description.length() > 300) {
              map.put("error_message", "Bot描述的长度不能大于300");
              return map;
          }
  
          if (content == null || content.length() == 0) {
              map.put("error_message", "代码不能为空");
              return map;
          }
  
          if (content.length() > 10000) {
              map.put("error_message", "代码长度不能超过10000");
              return map;
          }
  
          Bot bot = botMapper.selectById(bot_id);
  
          if (bot == null) {
              map.put("error_message", "Bot不存在或已被删除");
              return map;
          }
  
          if (!bot.getUserId().equals(user.getId())) {
              map.put("error_message", "没有权限修改该Bot");
              return map;
          }
  
          Bot new_bot = new Bot(
                  bot.getId(),
                  user.getId(),
                  title,
                  description,
                  content,
                  bot.getRating(),
                  bot.getCreatetime(),
                  new Date()
          );
  
          botMapper.updateById(new_bot);
  
          map.put("error_message", "success");
  
          return map;
      }
  }
  ```

- 创建 `controller/user/bot/UpdateController.java`

  ```java
  ...
  
  @RestController
  public class UpdateController {
      @Autowired
      private UpdateService updateService;
  
      @PostMapping("/user/bot/update/")
      public Map<String, String> update(@RequestParam Map<String, String> data) {
          return updateService.update(data);
      }
  }
  ```

`/user/bot/getlist/`：查询 `Bot` 列表

- 创建 service/user/bot/GetListService.java

  ```java
  ...
  
  public interface GetListService {
      List<Bot> getList();
  }

- 创建 service/impl/user/bot/GetListServiceImpl.java

  ```java
  ...
  
  @Service
  public class GetListServiceImpl implements GetListService {
      @Autowired
      private BotMapper botMapper;
  
      @Override
      public List<Bot> getList() {
          UsernamePasswordAuthenticationToken authenticationToken =
                  (UsernamePasswordAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
          UserDetailsImpl loginUser = (UserDetailsImpl) authenticationToken.getPrincipal();
          User user = loginUser.getUser();
  
          QueryWrapper<Bot> queryWrapper = new QueryWrapper<>();
          queryWrapper.eq("user_id", user.getId());
  
          return botMapper.selectList(queryWrapper);
      }
  }

- 创建 controller/user/bot/GetListController.java

  ```java
  ...
  
  @RestController
  public class GetListController {
      @Autowired
      private GetListService getListService;
  
      @GetMapping("/user/bot/getlist/")
      public List<Bot> getList() {
          return getListService.getList();
      }
  }
  ```


### *第2部分代码仓库地址*

https://github.com/NidoSen/KOB/tree/53bcc43299199666463175383e39c3ac8b9bc3eb

### 5.3 个人中心界面前端部分

先根据页面设计完成基本显示用户头像和bot列表的功能，views/user/bots/UserBotIndexView.vue

```vue
<template>
    <div class="container">
        <div class="row">
            <!-- 在左边显示用户头像，占3份 -->
            <div class="col-3">
                <div class="card" style="margin-top: 20px"><!-- 控制和导航栏的上边距为20px -->
                    <div class="card-body">
                        <img :src="$store.state.user.photo" alt="" style="width: 100%">
                    </div>
                </div>
            </div>
            <!-- 在右边显示用户头像，占9份 -->
            <div class="col-9">
                <div class="card" style="margin-top: 20px"><!-- 控制和导航栏的上边距为20px -->
                    <!-- -------右边头部为：“我的Bot”标题 + “创建Bot”按钮-------  -->
                    <div class="card-header">
                        <span style="font-size:130%">我的Bot</span>
                        <!--float-end表示向右对齐-->
                        <button type="button" class="btn btn-primary float-end">
                            创建Bot
                        </button>
                    </div>
                    <!-- -------右边内容为：所有的bot的信息记录 + 修改按钮 + 删除按钮 -------  -->
                    <div class="card-body">
                        <table class="table table-hover">
                            <thead>
                                <tr>
                                    <th>名称</th>
                                    <th>创建时间</th>
                                    <th>操作</th>
                                </tr>
                            </thead>
                            <tbody>
                                <tr v-for="bot in bots" :key="bot.id">
                                    <th>{{ bot.title }}</th>
                                    <th>{{ bot.createtime }}</th>
                                    <td>
                                        <!-- margin-right:10px 控制修改按钮和删除按钮不要紧贴 -->
                                        <button type="button" class="btn btn-secondary" style="margin-right:10px">修改</button>
                                        <button type="button" class="btn btn-danger">删除</button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref } from 'vue'
import $ from 'jquery'
import { useStore } from 'vuex'

export default {
    setup() {
        const store = useStore();
        let bots = ref([]);
        
        //获取所有bots信息
        const refresh_bots = () => {
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/getlist/",
                type: "get",
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    bots.value = resp;
                }
            })
        }
        
        //进入页面时，需要先执行一次获取所有bots信息
        refresh_bots();

        return {
            bots,
        }
    }
}
</script>

<style scoped></style>
```

实现创建Bot按钮，点击该按钮时出现创建Bot弹窗

```vue
<template>
...
	<!-- -------右边头部为：“我的Bot”标题 + “创建Bot”按钮-------  -->
    <div class="card-header">
        <span style="font-size:130%">我的Bot</span>
        <!-- float-end表示向右对齐 -->
        <button type="button" class="btn btn-primary float-end" data-bs-toggle="modal"
            data-bs-target="#add-bot-btn">
            创建Bot
        </button>
    </div>
    <!-- -------右边内容为：所有的bot的信息记录-------  -->
...
	<!-- 创建Bot Modal框 -->
    <div class="modal fade" id="add-bot-btn" tabindex="-1">
        <!-- modal-xl控制modal框变大 -->
        <div class="modal-dialog modal-xl">
            <div class="modal-content">
                <div class="modal-header">
                    <h1 class="modal-title fs-5">创建Bot</h1>
                    <button type="button" class="btn-close" data-bs-dismiss="modal"
                        aria-label="Close"></button>
                </div>
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="add-bot-title" class="form-label">名称</label>
                        <input v-model="botadd.title" type="text" class="form-control"
                            id="add-bot-title" placeholder="请输入Bot名称">
                    </div>
                    <div class="mb-3">
                        <label for="add-bot-description" class="form-label">简介</label>
                        <textarea v-model="botadd.description" class="form-control"
                            id="add-bot-description" rows="3" placeholder="请输入Bot简介"></textarea>
                    </div>
                    <div class="mb-3">
                        <label for="add-bot-code" class="form-label">代码</label>
                        <VAceEditor v-model:value="botadd.content" @init="editorInit" lang="c_cpp"
                            theme="textmate" style="height: 300px" :options="{
                                enableBasicAutocompletion: true, //启用基本自动完成
                                enableSnippets: true, // 启用代码段
                                enableLiveAutocompletion: true, // 启用实时自动完成
                                fontSize: 18, //设置字号
                                tabSize: 4, // 标签大小
                                showPrintMargin: false, //去除编辑器里的竖线
                                highlightActiveLine: true,
                            }" />
                    </div>
                </div>
                <div class="modal-footer">
                    <div class="error-message">{{ botadd.error_message }}</div>
                    <button type="button" class="btn btn-primary" @click="add_bot">创建</button>
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">取消</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import { ref, reactive } from 'vue' //ref对应响应式变量，reactive对应对象
import $ from 'jquery'
import { useStore } from 'vuex'
import { Modal } from 'bootstrap/dist/js/bootstrap'
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';
import 'ace-builds/src-noconflict/mode-c_cpp';
import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/ext-language_tools';

export default {
    components: {
        VAceEditor,
    },
    setup() {
        const store = useStore();
        let bots = ref([]);
        
        const botadd = reactive({
            title: "",
            description: "",
            content: "",
            error_message: "",
        });
        
        //获取所有bots信息
        ...
        
        //进入页面时，需要先执行一次获取所有bots信息
        ...
        
        //创建bot
        const add_bot = () => {
            botadd.error_message = ""; //清空报错信息
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/add/",
                type: "post",
                data: {
                    title: botadd.title,
                    description: botadd.description,
                    content: botadd.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        //清空modal框
                        botadd.title = "";
                        botadd.description = "";
                        botadd.content = "";
                        Modal.getInstance("#add-bot-btn").hide(); //modal框关闭
                        refresh_bots();
                    }
                    else {
                        botadd.error_message = resp.error_message;
                    }
                }
            })
        }

        return {
            bots,
            botadd,
            add_bot,
        }
    }
}
</script>

<style scoped>
div.error-message {
    color: red;
}
</style>
```

实现修改和删除Bot按钮，点击该按钮时修改或删除对应的Bot

```vue
<template>
...
	<!-- -------右边内容为：所有的bot的信息记录 + 修改按钮 + 删除按钮 -------  -->
    <div class="card-body">
        <table class="table table-hover">
            <thead>
                <tr>
                    <th>名称</th>
                    <th>创建时间</th>
                    <th>操作</th>
                </tr>
            </thead>
            <tbody>
                <tr v-for="bot in bots" :key="bot.id">
                    <th>{{ bot.title }}</th>
                    <th>{{ bot.createtime }}</th>
                    <td>
                        <button type="button" class="btn btn-secondary" style="margin-right:10px"
                            data-bs-toggle="modal"
                            :data-bs-target="'#update-bot-modal-' + bot.id">修改</button>
                        <button type="button" class="btn btn-danger" @click="remove_bot(bot)">删除</button>
                        <!-- Modal -->
                        <div class="modal fade" :id="'update-bot-modal-' + bot.id" tabindex="-1">
                            <div class="modal-dialog modal-xl">
                                <div class="modal-content">
                                    <div class="modal-header">
                                        <h1 class="modal-title fs-5">创建Bot</h1>
                                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                            aria-label="Close"></button>
                                    </div>
                                    <div class="modal-body">
                                        <div class="mb-3">
                                            <label for="add-bot-title" class="form-label">名称</label>
                                            <input v-model="bot.title" type="text" class="form-control"
                                                id="add-bot-title" placeholder="请输入Bot名称">
                                        </div>
                                        <div class="mb-3">
                                            <label for="add-bot-description" class="form-label">简介</label>
                                            <textarea v-model="bot.description" class="form-control"
                                                id="add-bot-description" rows="3"
                                                placeholder="请输入Bot简介"></textarea>
                                        </div>
                                        <div class="mb-3">
                                            <label for="add-bot-code" class="form-label">代码</label>
                                            <VAceEditor v-model:value="bot.content" @init="editorInit"
                                                lang="c_cpp" theme="textmate" style="height: 300px"
                                                :options="{
                                                    enableBasicAutocompletion: true, //启用基本自动完成
                                                    enableSnippets: true, // 启用代码段
                                                    enableLiveAutocompletion: true, // 启用实时自动完成
                                                    fontSize: 18, //设置字号
                                                    tabSize: 4, // 标签大小
                                                    showPrintMargin: false, //去除编辑器里的竖线
                                                    highlightActiveLine: true,
                                                }" />
                                        </div>
                                    </div>
                                    <div class="modal-footer">
                                        <div class="error-message">{{ bot.error_message }}</div>
                                        <button type="button" class="btn btn-primary"
                                            @click="update_bot(bot)">保存修改</button>
                                        <button type="button" class="btn btn-secondary"
                                            data-bs-dismiss="modal">取消</button>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </td>
                </tr>
            </tbody>
        </table>
    </div>
...
</template>

<script>
import { ref, reactive } from 'vue' //ref对应响应式变量，reactive对应对象
import $ from 'jquery'
import { useStore } from 'vuex'
import { Modal } from 'bootstrap/dist/js/bootstrap'
import { VAceEditor } from 'vue3-ace-editor';
import ace from 'ace-builds';
import 'ace-builds/src-noconflict/mode-c_cpp';
import 'ace-builds/src-noconflict/mode-json';
import 'ace-builds/src-noconflict/theme-chrome';
import 'ace-builds/src-noconflict/ext-language_tools';

export default {
    components: {
        VAceEditor,
    },
    setup() {
        ...
        
        const update_bot = (bot) => {
            bot.error_message = "";
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/update/",
                type: "post",
                data: {
                    bot_id: bot.id,
                    title: bot.title,
                    description: bot.description,
                    content: bot.content,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        Modal.getInstance('#update-bot-modal-' + bot.id).hide();
                        refresh_bots();
                    }
                    else {
                        bot.error_message = resp.error_message;
                    }
                }
            })
        }

        const remove_bot = (bot) => {
            $.ajax({
                url: "http://127.0.0.1:3000/user/bot/remove/",
                type: "post",
                data: {
                    bot_id: bot.id,
                },
                headers: {
                    Authorization: "Bearer " + store.state.user.token,
                },
                success(resp) {
                    if (resp.error_message === "success") {
                        refresh_bots();
                    }
                }
            })
        }

        return {
            bots,
            botadd,
            add_bot,
            update_bot,
            remove_bot,
        }
    }
}
</script>

<style scoped>
div.error-message {
    color: red;
}
</style>
```

另外为了解决跨时区时间显示错误的问题，后端的Bot.java需要修改，给日期加上时区限制

```java
...

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Bot {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer userId;
    private String title;
    private String description;
    private String content;
    private Integer rating;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date createtime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date modifytime;
}
```

