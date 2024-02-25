<template>
  <div>
    <PlayGround v-if="$store.state.pk.status === 'playing'" />
    <MatchGround v-if="$store.state.pk.status === 'matching'" />
    <ResultBoard v-if="$store.state.pk.loser !== 'none'" />
  </div>
</template>

<script>
import PlayGround from "../../components/PlayGround.vue";
import MatchGround from "../../components/MatchGround.vue";
import ResultBoard from "../../components/ResultBoard.vue";
import { onMounted, onUnmounted } from "vue"; //一个是组件挂载完毕，一个是组件销毁前
import { useStore } from "vuex";

export default {
  components: {
    PlayGround,
    MatchGround,
    ResultBoard,
  },
  setup() {
    const store = useStore();
    const socketUrl = `ws://127.0.0.1:3000/websocket/${store.state.user.token}/`; //注意这里是`不是单引号'

    let socket = null;
    //挂载完成，创建一个连接
    onMounted(() => {
      store.commit("updateOpponent", {
        username: "我的对手",
        photo:
          "https://cdn.acwing.com/media/article/image/2022/08/09/1_1db2488f17-anonymous.png",
      });
      socket = new WebSocket(socketUrl);

      //建立连接的时候
      socket.onopen = () => {
        console.log("connected!");
        store.commit("updateSocket", socket);
      };

      //接收到信息的时候
      socket.onmessage = (msg) => {
        //msg的格式是框架定义的
        const data = JSON.parse(msg.data);
        if (data.event === "start-matching") {
          store.commit("updateOpponent", {
            username: data.opponent_username,
            photo: data.opponent_photo,
          });
          setTimeout(() => {
            store.commit("updateStatus", "playing");
          }, 200);
          store.commit("updateGame", data.game);
        } else if (data.event === "move") {
          console.log(data);
          const game = store.state.pk.gameObject;
          const [snake0, snake1] = game.snakes;
          snake0.set_direction(data.a_direction);
          snake1.set_direction(data.b_direction);
        } else if (data.event === "result") {
          console.log(data);
          const game = store.state.pk.gameObject;
          const [snake0, snake1] = game.snakes;
          if (data.loser === "all" || data.loser === "A") {
            snake0.status = "die";
          }
          if (data.loser === "all" || data.loser === "B") {
            snake1.status = "die";
          }
          store.commit("updateLoser", data.loser);
        }
      };

      //关闭的时候
      socket.onclose = () => {
        //卸载的时候一定要断开，否则会产生冗余连接
        console.log("disconnected!");
      };
    });

    onUnmounted(() => {
      socket.close();
      store.commit("updateLoser", "none"); //关闭ResultBoard
      store.commit("updateStatus", "matching");
    });
  },
};
</script>

<style scoped></style>
