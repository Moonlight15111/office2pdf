<template>
    <div></div>
</template>

<script>
  export default {
    data () {
      return {
        row: undefined, // 文件所在行
        syncTaskId: undefined, // 异步任务Id
        syncTaskTimer: undefined, // 异步任务定时器
        fileName: undefined // 文件名
      }
    },
    methods: {
      // 预览
      async preview (id, fileName, row) {
        try {
          const data = await this.$get('/preview/create/' + id)
          if (this.isData(data)) {
            this.syncTaskId = data.syncTaskId
            this.syncTaskTimer = window.setInterval(this.querySyncTaskStatus, 2000)
            this.fileName = fileName
            this.row = row
            this.$emit('loadingCallBack', this.row, true)
          } else {
            this.$message.error(data.msg)
          }
        } catch (e) {
          console.error(e)
          this.$message.error('文件转换失败')
        }
      },
      // 查异步任务的状态
      async querySyncTaskStatus () {
        const data = await this.$get('/sync/querySyncInfoStatus/' + this.syncTaskId)
        if (this.isData(data)) {
          const status = data.status
          if (status === '成功') {
            this.doPreview()
            this.$emit('loadingCallBack', this.row, false)
          } else if (status === '出错'){
            this.$emit('loadingCallBack', this.row, false)
            this.syncTaskId = undefined
            window.clearInterval(this.syncTaskTimer)
            this.$message.error(data.msg)
          }
        }
      },
      // 预览
      async doPreview () {
        const { data } = await this.$http({
          url: this.$http.adornUrl(`/preview/pdf/` + this.syncTaskId),
          method: 'get',
          responseType: 'blob'
        })
        const dataUrl = window.URL.createObjectURL(data)
        this.syncTaskId = undefined
        window.clearInterval(this.syncTaskTimer)

        const html = "<iframe allowfullscreen width=\"100%\" height=\"100%\" name='" + this.fileName + "' frameborder=\"0\" type=\"application/pdf\" src='" + dataUrl + "'>"
        const newwindow = window.open()
        newwindow.document.write(html);
        newwindow.document.title = this.fileName
      }
    }
  }
</script>

<style scoped>

</style>
