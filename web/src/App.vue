<template>
  <NavBar />
  <router-view />
</template>

<script>

import NavBar from './components/NavBar.vue'
import "bootstrap/dist/css/bootstrap.min.css"
import "bootstrap/dist/js/bootstrap"
// import $ from 'jquery'

export default {
  components: {
    NavBar
  },
  // setup() {
  //   $.ajax({
  //     url: "http://127.0.0.1:3000/user/account/token/",
  //     type: "post",
  //     data: {
  //       username: 'b',
  //       password: 'pb',
  //     },
  //     success(resp) {
  //       console.log(resp);
  //     },
  //     error(resp) {
  //       console.log(resp);
  //     }
  //   })

  //   $.ajax({
  //     url: "http://127.0.0.1:3000/user/account/info/",
  //     type: "get",
  //     headers: {
  //       Authorization: "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJqdGkiOiJlOWI5OTQwMTUxNGM0M2ZmOTE0MzUwMDM5MGUwYmU1MSIsInN1YiI6IjIiLCJpc3MiOiJzZyIsImlhdCI6MTY5NTUzNTA3MCwiZXhwIjoxNjk2NzQ0NjcwfQ.NWvk4eHAgrm7PLXHAj2fFzKngutTyQYD2Xrkks1RtY0",
  //     },
  //     success(resp) {
  //       console.log(resp);
  //     },
  //     error(resp) {
  //       console.log(resp);
  //     }
  //   })

  //   $.ajax({
  //     url: "http://127.0.0.1:3000/user/account/register/",
  //     type: "post",
  //     data: {
  //       username: "yxc",
  //       password: "123",
  //       confirmedPassword: "123",
  //     },
  //     success(resp) {
  //       console.log(resp);
  //     },
  //     error(resp) {
  //       console.log(resp);
  //     }
  //   })
  // }
}
</script>

<style>
body {
  background-image: url("@/assets/images/maitian.jpg");
  background-size: cover;
}
</style>
