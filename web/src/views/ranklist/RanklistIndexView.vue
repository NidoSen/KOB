<template>
  <ContentField>
    <table class="table table-hover" style="text-align: center">
      <thead>
        <tr>
          <th>玩家</th>
          <th>天梯分</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="user in users" :key="user.id">
          <td>
            <img :src="user.photo" alt="" class="user-photo" />
            &nbsp;
            <span class="record-user-username">{{ user.username }}</span>
          </td>
          <td>
            {{ user.rating }}
          </td>
        </tr>
      </tbody>
    </table>
    <!-- bootstrap上提供的分页条 float: right 实现将分页条放置在右边-->
    <nav aria-label="..." style="float: right">
      <ul class="pagination">
        <li class="page-item">
          <a class="page-link" href="#" @click="click_page(-2)">前一页</a>
        </li>
        <li
          :class="'page-item ' + page.is_active"
          v-for="page in pages"
          :key="page.number"
        >
          <a class="page-link" href="#" @click="click_page(page.number)">
            {{ page.number }}
          </a>
        </li>
        <li class="page-item">
          <a class="page-link" href="#" @click="click_page(-1)">后一页</a>
        </li>
      </ul>
    </nav>
  </ContentField>
</template>

<script>
import ContentField from "../../components/ContentField.vue";
import { useStore } from "vuex";
import { ref } from "vue";
import $ from "jquery";

export default {
  components: {
    ContentField,
  },
  setup() {
    const store = useStore();
    let users = ref([]);
    let current_page = 1;
    let total_users = 0;
    let pages = ref([]);

    const click_page = (page) => {
      //点击分页条的“上一页”，“下一页”和数字
      if (page == -2) {
        //-2表示点击上一页
        page = current_page - 1;
      } else if (page == -1) {
        //-1表示点击下一页
        page = current_page + 1;
      }
      let max_pages = parseInt(Math.ceil(total_users / 3));
      if (page >= 1 && page <= max_pages) {
        pull_page(page); //如果点击有效，就重新拉取对局记录并更新页码
      }
    };

    const update_pages = () => {
      //点击分页条个选项后需要调整当前可选页面（范围为[当前页面-2, 当前页面+2]）
      let max_pages = parseInt(Math.ceil(total_users / 3));
      let new_pages = [];
      for (let i = current_page - 2; i <= current_page + 2; i++) {
        if (i >= 1 && i <= max_pages) {
          //可选页面的最小值和最大值有要求
          new_pages.push({
            number: i,
            is_active: i === current_page ? "active" : "",
          });
        }
      }
      pages.value = new_pages;
    };

    const pull_page = (page) => {
      current_page = page;
      $.ajax({
        url: "http://127.0.0.1:3000/ranklist/getlist/",
        type: "get",
        data: {
          page,
        },
        headers: {
          Authorization: "Bearer " + store.state.user.token,
        },
        success(resp) {
          users.value = resp.users;
          total_users = resp.users_count;
          update_pages(); //拉取对战记录后，更新当前可选页面
        },
        error(resp) {
          console.log(resp);
        },
      });
    };

    pull_page(current_page);

    return {
      users,
      pages,
      click_page,
    };
  },
};
</script>

<style scoped>
img.user-photo {
  width: 4vh;
  border-radius: 50%;
}
</style>
