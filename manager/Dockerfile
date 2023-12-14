###########
# Stage 1 #
###########
FROM openjdk:17-jdk-alpine as builder

# Install packages
RUN apk update && \
    apk --no-cache add openssl-dev cmake make gcc g++ git gmp-dev maven

# Download libgroupsig
RUN wget https://github.com/IBM/libgroupsig/archive/refs/tags/v1.1.0.tar.gz -O /tmp/libgroupsig.tar.gz && \
    tar -xzf /tmp/libgroupsig.tar.gz -C /tmp && \
    mkdir /tmp/libgroupsig-1.1.0/build

# Build (libgroupsig)
WORKDIR /tmp/libgroupsig-1.1.0/build
RUN cmake -DCMAKE_SHARED_LINKER_FLAGS="-Wl,--allow-multiple-definition" .. && \
    make

# Build (jgroupsig)
WORKDIR /tmp/libgroupsig-1.1.0/src/wrappers/java/jgroupsig/
RUN mvn clean package

###########
# Stage 2 #
###########
FROM openjdk:17-jdk-alpine

# Install packages
RUN apk update && \
    apk --no-cache add openssl gcc g++ gmp-dev

# Copy mcl, libgroupsig, jgroupsig
COPY --from=builder /tmp/libgroupsig-1.1.0/build/external/lib /usr/lib/mcl
COPY --from=builder /tmp/libgroupsig-1.1.0/build/lib /usr/lib/libgroupsig
COPY --from=builder /tmp/libgroupsig-1.1.0/src/wrappers/java/jgroupsig/java/target/main/resources /usr/lib/jgroupsig

# Set loader library path
ENV LD_LIBRARY_PATH=/usr/lib/jgroupsig:/usr/lib/libgroupsig:/usr/lib/mcl:$LD_LIBRARY_PATH

# Copy gradle files
COPY ./gradle /app/gradle
COPY ./gradlew /app/gradlew

# Download gradle wrapper
WORKDIR /app
RUN ./gradlew --version

# Keep the container alive
CMD ["tail", "-f", "/dev/null"]
