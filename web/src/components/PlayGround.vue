<template>
    <div class="playground">
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
    width: 60vw;
    /* 60%屏幕宽度 */
    height: 70vh;
    /* 70%屏幕高度 */
    margin: 40px auto;
    /* 居中+上边距40px */
}
</style>