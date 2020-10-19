# article
```
PUT /article
{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 1
  },
  "mappings": {
    "properties": {
      "name": {
        "type": "text",
        "analyzer": "ik_max_word"
      },
      "author": {
        "type": "keyword"
      },
      "wordcount": {
        "type": "long"
      },
      "onsale": {
        "type": "date",
        "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd"
      },
      "desc": {
        "type": "text",
        "analyzer": "ik_max_word"
      }
    }
  }
}

POST /article/_doc
{
  "name": "盘龙8",
  "author": "我不吃西红柿8",
  "wordcount": 10008,
  "onsale": "1985-01-08",
  "desc": "只有描述8盘龙8"
}








```

# map
```
# 地图经纬度搜索
PUT /map
{
  "settings": {
    "number_of_shards" : 1,
    "number_of_replicas": 1
  },
  "mappings": {
    "properties": {
      "name": {
        "type": "text",
        "analyzer": "ik_max_word"
      }, 
      "location": {
        "type": "geo_point"
      }
    }
  }
}


POST /map/_doc
{
  "name": "人民广场",
  "location": {
    "lon": 121.475164,
    "lat": 31.228816
  }
}

POST /map/_doc
{
  "name": "上海儿童医院",
  "location": {
    "lon": 121.386243,
    "lat": 31.227055
  }
}

POST /map/_doc
{
  "name": "红星美凯龙",
  "location": {
    "lon": 121.595456,
    "lat": 31.249144
  }
}

POST /map/_doc
{
  "name": "同济大学",
  "location": {
    "lon": 121.501385,
    "lat": 31.283332
  }
}

POST /map/_doc
{
  "name": "世博园",
  "location": {
    "lon": 121.484391,
    "lat": 31.186899
  }
}


POST /map/_doc
{
  "name": "城隍庙",
  "location": {
    "lon": 121.491772,
    "lat": 31.22566
  }
}

# geo 检索方式之一: geo_distance 半径检索方式，获取以此半径为圆内的全部数据
# geo 检索方式之二: geo_bounding_box: 以两个点确定一个矩形，获取矩形内的全部数据
# geo 检索方式之三: geo_polygon 以多个点确定一个多边形，获取这个多边形内的全部数据

# geo_distance
POST /map/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match_all": {}
        }
      ],
      "filter": [
        {
          "geo_distance": {
            "distance": "3km",
            "distance_type": "arc", 
            "location": {
              "lat": 31.228816,
              "lon": 121.475164
            }
          }
        }
      ]
    }
  }
}

# geo_bounding_box
POST /map/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match_all": {}
        }
      ],
      "filter": [
        {
          "geo_bounding_box": {
            "location": {
              "top_left": {
                "lat": 31.249291,
                "lon": 121.45538
              },
              "bottom_right": {
                "lat": 31.183815,
                "lon": 121.528679
              }
            }
          }
        }
      ]
    }
  }
}

# geo_polygon
POST /map/_search
{
  "query": {
    "bool": {
      "must": [
        {
          "match_all": {}
        }
      ],
      "filter": [
        {
          "geo_polygon": {
            "location": {
              "points": [
                {
                  "lat": 31.249878,
                  "lon": 121.455208
                },
                {
                  "lat": 31.237109,
                  "lon": 121.443364
                },
                {
                  "lat": 31.216705,
                  "lon": 121.442505
                },
                {
                  "lat": 31.220375,
                  "lon": 121.523186
                }
              ]
            }
          }
        }
      ]
    }
  }
}
```