export default {
    state: {
        is_record: false, //是否需要切换到对战记录复现界面
        a_steps: "", //a的操作序列
        b_steps: "", //b的操作序列
        record_loser: "", //谁输了（注意这里不能和pk.js里的loser重名，尽管两者属于不同的文件）
    },
    getters: {

    },
    mutations: {
        updateIsRecord(state, is_record) {
            state.is_record = is_record;
        },
        updateSteps(state, data) {
            state.a_steps = data.a_steps;
            state.b_steps = data.b_steps;
        },
        updateRecordLoser(state, record_loser) {
            state.record_loser = record_loser;
        }
    },
    modules: {}
}