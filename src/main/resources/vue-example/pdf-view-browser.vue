<template>
    <div></div>
</template>

<script>
  export default {
    data: function () {
      return {
        taskArr: []
      }
    },
    methods: {
      // 预览
      async preview (id, fileName, row) {
        for (const item of this.taskArr) {
          if (item.id === id && fileName === item.fileName) {
            return
          }
        }
        try {
          const data = await this.$get('/preview/create/' + id)
          if (this.isData(data)) {
            const item = {
              id: id,
              row: row, // 文件所在行
              syncTaskId: data.syncTaskId, // 异步任务Id
              syncTaskTimer: window.setInterval(this.querySyncTaskStatus, 2000, data.syncTaskId), // 异步任务状态查询定时器
              fileName: fileName // 文件名
            }
            this.taskArr.push(item)
            this.$emit('loadingCallBack', row, true)
          } else {
            this.$message.error(data.msg)
          }
        } catch (e) {
          console.error(e)
          this.$message.error('文件转换失败')
        }
      },
      // 查异步任务的状态
      async querySyncTaskStatus (syncTaskId) {
        const data = await this.$get('/sync/querySyncInfoStatus/' + syncTaskId)
        if (this.isData(data)) {
          const status = data.status
          if (status === '成功') {
            this.doPreview(syncTaskId)
          } else if (status === '出错'){
            this.cleanTaskAndCallBack(syncTaskId)
            this.$message.error(data.msg)
          }
        }
      },
      // 预览
      async doPreview (syncTaskId) {
        const { data } = await this.$http({
          url: this.$http.adornUrl(`/preview/pdf/` + syncTaskId),
          method: 'get',
          responseType: 'blob'
        })
        // let fileName = ''
        // for (const item of this.taskArr) {
        //    if (item.syncTaskId === syncTaskId) {
        //    fileName = item.fileName
        //    break
        //  }
        // }
        this.cleanTaskAndCallBack(syncTaskId)

        // Note:
        // 在Chrome中，文件名是根据URL派生来的. 所以只要是使用blob url的方式
        // 那么就不能设置Chrome里面显示的PDF文件名, 因为没办法控制分配给 blob 的UUID
        window.open(window.URL.createObjectURL(data))
        // const blobFile = new File([data], fileName, { type: 'application/pdf' })
        // const dataUrl = window.URL.createObjectURL(blobFile)
        // window.open(dataUrl, fileName)
      },
      // 清理定时任务并回调
      cleanTaskAndCallBack (syncTaskId) {
        const _this = this
        _this.taskArr.forEach(function (item, index, array) {
          if (item.syncTaskId === syncTaskId) {
            _this.$emit('loadingCallBack', item.row, false)

            item.syncTaskId = undefined
            window.clearInterval(item.syncTaskTimer)

            array.splice(index, 1)
          }
        })
      }
    }
  }
</script>

<style scoped>
</style>