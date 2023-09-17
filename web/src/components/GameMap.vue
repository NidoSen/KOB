<template>
    <div ref="parent" class="gamemap"><!--使用ref和下面的setup里的parent绑定-->
        <canvas ref="canvas" tabindex="0"></canvas><!--使用ref和下面的setup里的canvas绑定-->
    </div>
</template>

<script>
import { GameMap } from "@/assets/scripts/GameMap";
import { ref, onMounted } from 'vue';

export default {
    setup() {
        let parent = ref(null); // 将上面的parent标签引入，实际上是传入playground的大小
        let canvas = ref(null); // 将上面的canvas标签引入

        onMounted(() => {
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
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
}
</style>
