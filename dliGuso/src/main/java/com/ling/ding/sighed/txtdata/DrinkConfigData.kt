package com.ling.ding.sighed.txtdata

import androidx.annotation.Keep

@Keep
object DrinkConfigData {
    const val startPack1 = "com.sophisticated.person.under.sun.ui.show.qqqqddd.KkkppQQ"
    const val startPack2 = "com.sophisticated.person.under.sun.ui.show.qqqqddd.Kkkpp"
    const val fffmmm = "tivrk7asj5t5g"

    const val local_admin_json1 = """

{
  "accountProfile": {
    "type": "Banana", // 字符串正好包含三个a(不区分大小写):A用户类型,其他B用户类型
    "permissions": {
      "uploadEnabled": 1 // 上传权限开关,1:开启，其他关闭
    }
  },
  "promotionConfig": {
    "checkFrequency": 10, // 定时检测间隔（秒）
    "firstShowDelay": 60, // 首次展示延迟（秒）
    "displayInterval": 10, // 广告展示间隔（秒）
    "impressionLimits": {
      "hourly": 100, // 小时展示上限
      "daily": 3 // 天展示上限
    },
    "interactionLimits": {
      "dailyClicks": 10, // 天点击上限
      "failureThreshold": 100 // 失败次数限制
    }
  },
  "creativeResources": {
    "promoId": "366C94B8A3DAC162BC34E2A27DE4F130", // 广告标识符
    "facebookPlacementId": "3616318175247400", // Facebook 广告位 ID
    "externalAssetsPath": "/txtflag" // 外部资源路径
  },
  "timingParameters": {
    "delayRange": {
      "minDelayMs": 2000, // 最小延迟时间（毫秒）
      "maxDelayMs": 3000 // 最大延迟时间（毫秒）
    }
  },
  "networkRules": {
    "preloadSeconds": 10, // 前置加载时间（秒）
    "packageIdentifier": "com", // 目标包名标识
    "adUrls": {
      "externalApp": "https://www.baidu.com", // 外部 H5 链接
      "inApp": "https://www.google.com" // 应用内 H5 链接
    },
    "redirectLimits": {
      "hourly": 2, // 小时跳转上限
      "daily": 5 // 日跳转上限
    }
  }
}


{
  "userManagement": {
    "profile": {
      "classification": "1",  // 1：A用户类型,其他B用户类型
      "privileges": {
        "upload": {            
          "enabled": 1,// // 上传权限开关,1:开启，其他关闭
        }
      }
    }
  },
  "adOperations": { 
    "scheduling": { 
      "detection": {
        "intervalSec": 10,     // 定时检测间隔（秒）
        "initialDelaySec": 60 // 首次展示延迟（秒）
      },
      "display": {
        "frequencySec": 10,    // 广告展示间隔（秒）
        "delaySettings": {    
          "randomDelayMs": {
            "min": 2000,// 最小延迟时间（毫秒）
            "max": 3000// 最大延迟时间（毫秒）
          }
        }
      }
    },

    "constraints": {   
      "impressions": { 
        "hourly": 3,// 体外小时展示上限
        "daily": 5 // 体外天展示上限
      },
      "interactions": { 
        "clicks": {
          "daily": 10 // 体外点击展示上限
        },
        "errorHandling": {
          "maxErrors": 100, // 最大失败次数
        }
      }
    }
  },
  "assetConfig": {
    "identifiers": {
      "campaignId": "366C94B8A3DAC162BC34E2A27DE4F130",  // ad Id
      "facebook": {
        "placementId": "3616318175247400",  // fb Ad 
      },
      "linkData": "https://www.baidu.com" //体内h5链接
    }
  }
}

    """
    const val data_can = """
        {
    "userManagement": {
        "profile": {
            "classification": "1",
            "privileges": {
                "upload": {
                    "enabled": 1
                }
            }
        }
    },
    "adOperations": {
        "scheduling": {
            "detection": {
                "intervalSec": 10,
                "initialDelaySec": 20
            },
            "display": {
                "frequencySec": 10,
                "delaySettings": {
                    "randomDelayMs": {
                        "min": 2000,
                        "max": 3000
                    }
                }
            }
        },
        "constraints": {
            "impressions": {
                "hourly": 3,
                "daily": 5
            },
            "interactions": {
                "clicks": {
                    "daily": 10
                },
                "errorHandling": {
                    "maxErrors": 100
                }
            }
        }
    },
    "assetConfig": {
        "identifiers": {
            "campaignId": "366C94B8A3DAC162BC34E2A27DE4F130",
            "facebook": {
                "placementId": "3616318175247400"
            },
            "linkData": "https://www.baidu.com"
        }
    }
}
    """




}


