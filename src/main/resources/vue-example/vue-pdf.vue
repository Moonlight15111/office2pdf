<template>
  <div>
    <el-button type="primary" icon="el-icon-arrow-left" @click="pageDown" :disabled="page === 1">上一页</el-button>
    <el-button type="primary" @click="pageUp" :disabled="page === numPages">下一页<i class="el-icon-arrow-right el-icon--right"></i></el-button>
    <el-input type="number" v-model.number="page2" style="width: 10em;margin-bottom: 10px;margin-left: 10px;margin-right: 10px;" @keyup.enter.native="getPage" ref="pageInput">
      <template slot="append">/{{numPages}}</template>
    </el-input>
    <el-button type="primary" @click="rotate -= 90">左转 &#x27F2;</el-button>
    <el-button type="primary" @click="rotate += 90">右转 &#x27F3;</el-button>
    <div style="width: 100%;" align="center">
      <div v-if="loadedRatio > 0 && loadedRatio < 1" style="background-color: green; color: white; text-align: center" :style="{ width: loadedRatio * 100 + '%' }">{{ Math.floor(loadedRatio * 100) }}%</div>
      <pdf ref="pdf" style="border: 1px solid" :src="pdfUrl" :page="page" :rotate="rotate" @progress="loadedRatio = $event" @num-pages="numPages = $event" @link-clicked="page = $event"></pdf>
    </div>
  </div>
</template>

<script>
  // 首先需要运行 cnpm install --save vue-pdf 安装vue-pdf
  import pdf from 'vue-pdf'
  export default {
    name: 'qt-pdf',
    props: {
      pdfUrl: {
        type: String,
        required: false,
        default: ''
      }
      // page: {
      //   type: Number,
      //   required: false,
      //   default: 1
      // }
    },
    components: {
      pdf: pdf
    },
    data () {
      return {
        pageInput: '',
        loadedRatio: 0,
        page: 1,
        page2: 1,
        numPages: 0,
        rotate: 0,
        pdf
      }
    },
    methods: {
      getPage () {
        this.page = this.page2
      },
      pageUp () {
        this.page += 1
        this.page2 = this.page
      },
      pageDown () {
        this.page -= 1
        this.page2 = this.page
      }
    }
  }
</script>

<style scoped>

</style>
