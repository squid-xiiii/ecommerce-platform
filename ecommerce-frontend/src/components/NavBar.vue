<template>
  <el-menu mode="horizontal" :router="true">
    <el-menu-item index="/">首页</el-menu-item>
    <el-menu-item index="/goods">商品列表</el-menu-item>
    <el-menu-item index="/user/zhangsan">我的信息</el-menu-item>
    <el-menu-item index="/orders/zhangsan">我的订单</el-menu-item>
    <el-menu-item index="/dashboard">统计看板</el-menu-item>
  </el-menu>
</template>