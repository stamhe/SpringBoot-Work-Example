# 常用命令
```
curl -XGET "localhost:9200/_cat/indices?pretty"
curl -XGET "localhost:9200/_cat/health?pretty"
curl -XGET "localhost:9200/_cat/nodes?pretty"
curl -XDELETE "localhost:9200/person3"
curl -XPUT -H 'Content-Type: application/json' "localhost:9200/person3?pretty" -d '{"settings":{"number_of_shards":3,"number_of_replicas":1}}'
curl -XGET "localhost:9200/person3"
curl -XGET "localhost:9200/bank/_search?q=Virginia&pretty"
curl -XGET "localhost:9200/bank/_search?q=firstname:Virginia&sort=account_number:asc&pretty"
curl -XGET "http://localhost:9200/book_20201210/_doc/pDXJS3YBqhYaewch6KkB?pretty"
curl -XGET -H 'Content-Type: application/json' "localhost:9200/_analyze" -d '{"analyzer": "ik_smart","text": "今天天气真好"}'
curl -XGET -H "Content-Type: application/json" "localhost:9200/bank/_search?pretty" -d '{"query":{"term":{"address":"Avenue"}}}'
curl -XGET -H "Content-Type: application/json" "localhost:9200/book_20201210/_search?pretty" -d '{"query":{"terms":{"author": ["我吃西红柿", "西红柿"]}}}'
curl -XGET -H "Content-Type: application/json" "localhost:9200/bank/_search?pretty" -d '{"query":{"match":{"address":"Avenue"}}}'
curl -XPOST -H 'Content-Type: application/json' "localhost:9200/book_20201210/_search?scroll=10m&pretty" -d '{"query":{"match_all":{}},"size":3,"sort":[{"wordcount":{"order":"desc"}}]}'
curl -XPOST -H 'Content-Type: application/json' "localhost:9200/_search?pretty" -d '{"scroll_id":"","scroll":"10m"}'
```
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


PUT /book_20201210
 {
   "settings": {
     "number_of_replicas": 1,
     "number_of_shards": 1
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
         "format": "yyyy-MM-dd HH:mm:ss||yyyy-MM-dd||epoch_millis"
       },
       "desc": {
         "type": "text",
         "analyzer": "ik_max_word"
       }
     }
   }
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