import router from '@/router/index.js'
import {getMenuAuthTree} from '@/api/menu.js'
import {defineStore} from "pinia";
import {constantRoutes, dynamicRoutes} from "@/router/routes.js";
import _ from "lodash-es";

const modules = import.meta.glob('@/views/**/*.vue')

const usePermissionStore = defineStore('permission', {
    state: () => ({
        sidebarRouters: []
    }),
    actions: {
        setSidebarRouters(routes) {
            this.sidebarRouters = routes
        },
        generateRoutes() {
            return new Promise(resolve => {
                getMenuAuthTree().then(res => {
                    const sdata = _.cloneDeep(res.data)
                    const rdata = _.cloneDeep(res.data)
                    const sidebarRoutes = filterAsyncRouter(sdata)
                    const rewriteRoutes = filterAsyncRouter(rdata, false, true)
                    dynamicRoutes.forEach(route => {
                        router.addRoute(route)
                    })
                    this.setSidebarRouters(constantRoutes.concat(sidebarRoutes))
                    resolve(rewriteRoutes)
                })
            })
        }
    },
    persist: true
})

// 遍历后台传来的路由字符串，转换为组件对象
const filterAsyncRouter = (asyncRouterMap, lastRouter = false, type = false) => {
    return asyncRouterMap.filter(route => {
        if (type && route.children) {
            route.children = filterChildren(route.children)
        }
        if (route.component) {
            if (route.component === '/') {
                route.component = () => import('@/views/backend/index.vue')
            } else {
                route.component = loadView(route.component)
            }
        }
        if (route.children != null && route.children && route.children.length) {
            route.children = filterAsyncRouter(route.children, route, type)
        } else {
            delete route['children']
            delete route['redirect']
        }
        return true
    })
}

const filterChildren = (childrenMap, lastRouter = false) => {
    let children = []
    childrenMap.forEach((el, index) => {
        if (el.children && el.children.length) {
            if (el.component === 'ParentView' && !lastRouter) {
                el.children.forEach(c => {
                    c.path = el.path + '/' + c.path
                    if (c.children && c.children.length) {
                        children = children.concat(filterChildren(c.children, c))
                        return
                    }
                    children.push(c)
                })
                return
            }
        }
        if (lastRouter) {
            el.path = lastRouter.path + '/' + el.path
            if (el.children && el.children.length) {
                children = children.concat(filterChildren(el.children, el))
                return
            }
        }
        children = children.concat(el)
    })
    return children
}

export const loadView = (view) => {
    return () => modules[`/src/views${view}`]()
}

export default usePermissionStore