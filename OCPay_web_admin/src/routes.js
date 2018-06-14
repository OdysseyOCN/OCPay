import Login from './views/Login.vue'
import NotFound from './views/404.vue'
import Home from './views/Home.vue'
import banner from './views/banner/Banner.vue'


let became = true;

let routes = [
    {
        path: '/login',
        component: Login,
        name: '',
        hidden: true
    },
    {
        path: '/404',
        component: NotFound,
        name: '',
        hidden: true
    },

    {
        path: '/',
        component: Home,
        name: 'version',
        iconCls: 'el-icon-setting',
        children: [
            { path: '/version', component: resolve=>require(['./views/version/Version.vue'],resolve), name: 'version' },
            
        ]
    },
   

    // Homepage
    {
        path: '/',
        component: Home,
        name: 'Homepage',
        iconCls: 'el-icon-setting',
        children: [
            { path: '/Homepage', component: resolve=>require(['./views/homepage/Homepage.vue'],resolve), name: 'Homepage' },
            
        ]
    },
    // Banner
    {
        path: '/',
        component: Home,
        name: 'Banner',
        iconCls: 'el-icon-setting',
        children: [
            { path: '/banner', component: banner, name: '横屏广告' },
            { path: '/slide', component: resolve=>require(['./views/slide/slide.vue'],resolve), name: '侧栏广告' },
            { path: '/program', component: resolve=>require(['./views/program/program.vue'],resolve), name: '小程序' },
        ]
    },

    // 账号管理
    {
        path: '/',
        component: Home,
        name: '账户管理',
        iconCls: 'el-icon-setting',
        children: [
            { path: '/AddUser', component: resolve=>require(['./views/User/AddUser.vue'],resolve), name: '新建用户' },
            { path: '/User', component: resolve=>require(['./views/User/User.vue'],resolve), name: '修改密码' },
            { path: '/AccountList', component: resolve=>require(['./views/User/AccountList.vue'],resolve), name: '账户列表' },
            { path: '/Feedback', component: resolve=>require(['./views/User/Feedback.vue'],resolve), name: '用户反馈' },
            // { path: '/echarts', component: echarts, name: 'echarts' }
        ]
    },
    
    {
        path: '*',
        hidden: became,
        redirect: { path: '/404' }
    }
];

export default routes;