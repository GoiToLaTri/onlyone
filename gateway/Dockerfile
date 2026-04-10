FROM node:22.22.0-alpine AS deps
WORKDIR /app

COPY gateway/package.json ./package.json
COPY gateway/yarn.lock ./yarn.lock
RUN yarn --frozen-lockfile

FROM node:22.22.0-alpine AS build
WORKDIR /app

COPY --from=deps /app/node_modules ./node_modules
COPY gateway .
RUN yarn build

FROM node:22.22.0-alpine AS runner
WORKDIR /app
ENV NODE_ENV=production

COPY --from=build /app/dist ./dist
COPY --from=build /app/node_modules ./node_modules
COPY --from=build /app/package.json ./package.json

CMD ["node", "dist/main"]