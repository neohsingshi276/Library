import { createWebHistory, createRouter } from 'vue-router'
/* Layout */
import Layout from '@/layout'

/**
 * Note: Route Configuration
 *
 * hidden: true                     // When set to true, this route will not appear in the sidebar, such as 401, login pages, or edit pages like /edit/1
 * alwaysShow: true                 // When a route has more than 1 child route declared, it will automatically become nested mode--like component pages
 *                                  // When there's only one, that child route will be displayed as the root route in the sidebar--like guide pages
 *                                  // If you want to always show your root route regardless of the number of children declared
 *                                  // You can set alwaysShow: true, which will ignore the previously defined rules and always show the root route
 * redirect: noRedirect             // When set to noRedirect, this route cannot be clicked in breadcrumb navigation
 * name:'router-name'               // Set the route name, must be filled otherwise there will be various issues when using <keep-alive>
 * query: '{"id": 1, "name": "ry"}' // Default parameters passed when accessing the route
 * roles: ['admin', 'common']       // Role permissions to access the route
 * permissions: ['a:a:a', 'b:b:b']  // Menu permissions to access the route
 * meta : {
    noCache: true                   // If set to true, it will not be cached by <keep-alive> (default false)
    title: 'title'                  // Set the name displayed in the sidebar and breadcrumb for this route
    icon: 'svg-name'                // Set the icon for this route, corresponding to path src/assets/icons/svg
    breadcrumb: false               // If set to false, it will not be displayed in the breadcrumb
    activeMenu: '/system/user'      // When the route sets this property, the corresponding sidebar will be highlighted.
  }
 */

// Public routes
export const constantRoutes = [
  {
    path: '/redirect',
    component: Layout,
    hidden: true,
    children: [
      {
        path: '/redirect/:path(.*)',
        component: () => import('@/views/redirect/index.vue')
      }
    ]
  },
  {
    path: '/login',
    component: () => import('@/views/login'),
    hidden: true
  },
  {
    path: '/emailLogin',
    component: () => import('@/views/emailLogin'),
    hidden: true
  },
  {
    path: '/register',
    component: () => import('@/views/register'),
    hidden: true
  },
  {
    path: "/:pathMatch(.*)*",
    component: () => import('@/views/error/404'),
    hidden: true
  },
  {
    path: '/401',
    component: () => import('@/views/error/401'),
    hidden: true
  },
  {
    path: '',
    component: Layout,
    redirect: '/index',
    children: [
      {
        path: '/index',
        component: () => import('@/views/index'),
        name: 'Index',
        meta: { title: 'Home', affix: true }
      }
    ]
  },
  {
    path: '',
    component: Layout,
    redirect: 'message',
    children: [
      {
        path: 'message',
        component: () => import('@/views/system/message/index'),
        name: 'Message',
        meta: { title: 'Announcements',  affix: true }
      },
    ]
  },
  {
    path: '/product',
    component: Layout,
    children: [
      {
        path: 'ai-publish',
        component: () => import('@/views/product/AiPublish.vue'),
        name: 'AiPublish',
        meta: { title: 'Publish Product', icon: '' }
      },
      {
        path: 'my-products',
        component: () => import('@/views/product/MyProducts.vue'),
        name: 'MyProducts',
        meta: { title: 'My Products', icon: 'shopping-bag' },
        hidden: true
      }
    ]
  },
  {
    path: '/product',
    component: Layout,
    children: [
      {
        path: 'mall',
        component: () => import('@/views/product/Mall.vue'),
        name: 'Mall',
        meta: { title: 'Shopping Mall', icon: 'shopping-cart', affix: true }
      },
      {
        path: 'favorite',
        component: () => import('@/views/product/Favorite.vue'),
        name: 'Favorite',
        meta: { title: 'My Favorites', icon: 'star', affix: false },
        hidden: true
      },
      {
        path: 'order',
        component: () => import('@/views/product/Order.vue'),
        name: 'Order',
        meta: { title: 'Confirm Order', icon: '', hidden: true },
        hidden: true
      },
      {
        path: 'payment',
        component: () => import('@/views/product/Payment.vue'),
        name: 'Payment',
        meta: { title: 'Order Payment', icon: '', hidden: true },
        hidden: true
      },
      {
        path: 'exchange',
        component: () => import('@/views/product/Exchange.vue'),
        name: 'Exchange',
        meta: { title: 'My Exchanges', icon: 'switch', affix: false },
        hidden: true
      },
    ]
  },
  {
    path: '/order',
    component: Layout,
    children: [
      {
        path: 'list',
        component: () => import('@/views/order/OrderList.vue'),
        name: 'OrderList',
        meta: { title: 'My Orders', icon: 'shopping-bag' }
      },
      {
        path: 'detail/:orderId',
        component: () => import('@/views/order/OrderDetail.vue'),
        name: 'OrderDetail',
        meta: { title: 'Order Details', icon: '', hidden: true },
        hidden: true
      }
    ]
  },
  {
    path: '/order',
    component: Layout,
    children: [
      {
        path: 'return/list',
        component: () => import('@/views/order/ReturnList.vue'),
        name: 'ReturnList',
        meta: { title: 'Returns/Refunds', icon: '' }
      },
      {
        path: 'return/:returnId',
        component: () => import('@/views/order/ReturnDetail.vue'),
        name: 'ReturnDetail',
        meta: { title: 'Return Details', icon: '', hidden: true },
        hidden: true
      }
    ]
  },
  {
    path: '/user',
    component: Layout,
    hidden: true,
    redirect: 'noredirect',
    children: [
      {
        path: 'profile',
        component: () => import('@/views/system/user/profile/index'),
        name: 'Profile',
        meta: { title: 'Profile', icon: 'user' }
      },
      {
        path: 'message/:noticeId',
        component: () => import('@/views/system/message/messageInfo'),
        name: 'messageDetail',
        props: true,
        meta: { title: 'Message Details' }
      },
    ]
  }
]

// Dynamic routes, loaded dynamically based on user permissions
export const dynamicRoutes = []

const router = createRouter({
  history: createWebHistory(),
  routes: constantRoutes,
  scrollBehavior(to, from, savedPosition) {
    if (savedPosition) {
      return savedPosition
    } else {
      return { top: 0 }
    }
  },
});

export default router;
