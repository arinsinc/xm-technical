## Crypto Recommendation Service

### Technical Details
* Database: H2
* Language/Framework: Java-21/Spring Boot
* Docker Supported
* Default port: 8080
* REST API documentation: http://localhost:8080/swagger-ui/index.html

### Implementation Details
* Six endpoints (3 mandatory + 3 extended)
* Rate Limit support (Default is 10 requests per minute)

### How to run
Run these commands from the project's root directory:

```shell
docker build -t crypto:v1 .
```
```shell
docker run -p 8080:8080 -d -t --name xm crypto:v1
```

### What is implemented
#### Requirements for the recommendation service:
- ✅ Reads all the prices from the csv files
- ✅ Calculates oldest/newest/min/max for each crypto for the whole month
- ✅ Exposes an endpoint that will return a descending sorted list of all the cryptos,
comparing the normalized range (i.e. (max-min)/min)
- ✅ Exposes an endpoint that will return the oldest/newest/min/max values for a requested
crypto
- ✅ Exposes an endpoint that will return the crypto with the highest normalized range for a
specific day
#### Things to consider:
- ✅ Documentation is our best friend, so it will be good to share one for the endpoints
- ✅ Initially the cryptos are only five, but what if we want to include more? Will the
recommendation service be able to scale?
- ✅ New cryptos pop up every day, so we might need to safeguard recommendations service
endpoints from not currently supported cryptos
- ✅ For some cryptos it might be safe to invest, by just checking only one month's time
frame. However, for some of them it might be more accurate to check six months or even
a year. Will the recommendation service be able to handle this?

#### Extra mile for recommendation service (optional):
- ✅ In XM we run everything on Kubernetes, so containerizing the recommendation service
will add great value
- ✅ Malicious users will always exist, so it will be really beneficial if at least we can rate limit
them (based on IP)