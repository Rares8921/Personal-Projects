# Shopplate Astro

<div align="center">

![Astro](https://img.shields.io/badge/Astro-5.8-BC52EE?style=for-the-badge&logo=astro&logoColor=white)
![React](https://img.shields.io/badge/React-19.1-61DAFB?style=for-the-badge&logo=react&logoColor=black)
![TypeScript](https://img.shields.io/badge/TypeScript-5.0-3178C6?style=for-the-badge&logo=typescript&logoColor=white)
![Tailwind CSS](https://img.shields.io/badge/Tailwind-4.1-06B6D4?style=for-the-badge&logo=tailwindcss&logoColor=white)
![Shopify](https://img.shields.io/badge/Shopify-Storefront_API-96BF48?style=for-the-badge&logo=shopify&logoColor=white)

**A production-ready e-commerce storefront powered by Shopify Storefront API and Astro. Full cart system, checkout, authentication, and product management.**

[Features](#what-it-does) • [Tech Stack](#tech-stack) • [Quick Start](#getting-started)

</div>

---

## What It Does

This is a complete e-commerce platform that integrates directly with Shopify's Storefront API through GraphQL. Built with Astro's server-side rendering for blazing-fast performance and SEO optimization.

**The good stuff:**
- **Dynamic Product Catalog** - Real-time product data from Shopify Storefront API via GraphQL
- **Shopping Cart** - Full cart functionality with add/remove/update quantities, persistent across sessions
- **Secure Checkout** - Complete Shopify checkout integration with payment processing
- **User Authentication** - Register, login, and manage user accounts
- **Advanced Search & Filtering** - Search products, filter by category/vendor/price range, sort options
- **Product Variants** - Size, color, and variant selection with real-time availability
- **Infinite Scroll** - Lazy load products on scroll for better performance
- **Wishlist System** - Save favorite products for later
- **Product Details** - Full product pages with image zoom, hover effects, image slider, and tabbed descriptions
- **Dark Mode** - Automatic light/dark theme switching with system preference detection
- **Responsive Design** - Mobile-first design that works perfectly on all devices
- **SEO Optimized** - Server-side rendering with Astro for excellent search engine visibility
- **Contact Forms** - Support inquiries with form validation
- **Content Management** - Write content in Markdown/MDX with auto-import components

**What makes it different:**
- Built on Astro's islands architecture - ship less JavaScript, load faster
- Server-side rendering (SSR) for dynamic Shopify data
- 99% Google PageSpeed score - optimized for performance
- GraphQL API queries for efficient data fetching
- Nanostores for lightweight state management (cart persistence)
- Tailwind CSS 4 with custom plugin architecture
- TypeScript throughout for type safety
- Production-ready deployment configs for Vercel and Netlify

The platform handles everything from browsing products to completing purchases through Shopify's secure checkout. Just connect your Shopify store and you have a modern, fast storefront ready to sell.

---

## Tech Stack

**Frontend Framework:** Astro 5.8 (SSR mode)  
**UI Library:** React 19.1 (Islands architecture)  
**Styling:** Tailwind CSS 4.1 + Custom Plugins  
**Language:** TypeScript 5  
**State Management:** Nanostores 1.0 + React bindings  
**API Integration:** Shopify Storefront API (GraphQL)  
**Content:** MDX + Markdown with remark plugins  
**Deployment:** Vercel (primary) + Netlify support  
**Build Tool:** Vite 6 with Astro integration

### Architecture

Modern JAMstack architecture with server-side rendering and API-first design:

```
Browser Client
      ↓
Astro SSR (Server-Side Rendering)
      ↓
React Islands (Hydrated Components)
      ↓
Nanostores (Cart State Management)
      ↓
GraphQL Queries
      ↓
Shopify Storefront API
      ↓
Products, Collections, Checkout, Customer Data
```

**Data Flow:**

```
Product Browsing:
User Request → Astro Page → GraphQL Query → Shopify API
           ↓
Product Data → SSR Rendering → HTML + JSON
           ↓
Client Hydration → React Islands → Interactive Features

Cart Management:
Add to Cart → Nanostores Update → Persisted to Cookies
          ↓
Cart State → Shared Across Pages → Checkout Creation
          ↓
Shopify Checkout API → Secure Payment → Order Confirmation
```

**How the core features work:**

**Shopify Integration:**
- GraphQL queries fetch products, collections, and customer data
- Storefront API token authenticates all requests
- Cart operations create checkout sessions in Shopify
- Checkout redirects to Shopify's hosted secure payment flow

**Cart System:**
```typescript
// Nanostores cart state
export const cartStore = atom<CartItem[]>([]);

// Add to cart
function addToCart(product: Product, variant: Variant) {
    cartStore.set([...cartStore.get(), { product, variant, quantity: 1 }]);
    // Persist to cookies for cross-session cart
    Cookies.set('cart', JSON.stringify(cartStore.get()));
}
```

**Product Filtering:**
- Client-side filtering for instant results
- URL parameters maintain filter state (shareable links)
- Combine multiple filters: category + price range + vendor
- Real-time result count updates

**Authentication Flow:**
1. User registers/logs in via Shopify Customer API
2. Customer token stored in secure httpOnly cookie
3. Token validates protected routes (account, orders, wishlist)
4. Middleware checks authentication on server-side

**Infinite Scroll:**
```typescript
// Intersection Observer for lazy loading
const observer = new IntersectionObserver((entries) => {
    if (entries[0].isIntersecting) {
        loadMoreProducts(); // Fetch next page from Shopify
    }
});
```

---

## Project Structure

```
shopplate-astro/
├── src/
│   ├── pages/               # Astro pages (SSR routes)
│   │   ├── index.astro     # Homepage
│   │   ├── products/       # Product catalog & details
│   │   │   ├── index.astro # Product listing page
│   │   │   └── [slug].astro # Dynamic product details
│   │   ├── login.astro     # User authentication
│   │   ├── sign-up.astro   # User registration
│   │   ├── contact.astro   # Contact form
│   │   ├── about.astro     # About page
│   │   ├── 404.astro       # Custom error page
│   │   └── api/            # API endpoints
│   ├── layouts/            # Page layout components
│   ├── content/            # MDX content files
│   ├── lib/                # Utility functions & helpers
│   │   └── shopify.ts      # Shopify API client
│   ├── cartStore.ts        # Nanostores cart state
│   ├── config/             # Site configuration
│   ├── styles/             # Global styles & Tailwind
│   └── types/              # TypeScript type definitions
├── public/                 # Static assets
├── config/                 # Astro & build configs
├── astro.config.mjs        # Astro configuration
├── tailwind.config.js      # Tailwind CSS config
├── tsconfig.json           # TypeScript config
├── package.json            # Dependencies
├── .env                    # Environment variables (API keys)
├── Dockerfile              # Docker container config
├── docker-compose.yml      # Docker Compose setup
└── netlify.toml           # Netlify deployment config
```

**Key Files:**
- **cartStore.ts:** Nanostores atom for cart state management
- **lib/shopify.ts:** GraphQL client and query functions for Shopify API
- **astro.config.mjs:** SSR mode, React integration, Vercel adapter
- **.env:** Shopify API credentials (Storefront API token, store domain)

---

## Getting Started

**What you need:**
- Node.js 20.10+ and npm 10.2+
- Shopify Partner account (free at https://partners.shopify.com/)
- Shopify Storefront API access token

**Setup (10 minutes):**

```bash
# 1. Navigate to project
cd "d:\Personal-Projects\Web projects\shopplate-astro"

# 2. Install dependencies
npm install

# 3. Create .env file with Shopify credentials
# Add these to .env:
# SHOPIFY_STORE_DOMAIN=your-store.myshopify.com
# SHOPIFY_STOREFRONT_ACCESS_TOKEN=your_storefront_api_token
# PUBLIC_SHOPIFY_STORE_DOMAIN=your-store.myshopify.com
```

**Get Shopify API Credentials:**

1. **Create Shopify Partner Account:**
   - Go to https://partners.shopify.com/
   - Sign up for free partner account
   - Create a development store

2. **Import Demo Products:**
   - In Shopify admin → Products → Import
   - Upload the `products.csv` from `public/` folder
   - Wait for import to complete

3. **Get Storefront API Token:**
   - Shopify Admin → Settings → Apps and sales channels
   - Click "Develop apps"
   - Create new app with any name
   - Configure Storefront API access
   - Enable these permissions:
     - `unauthenticated_read_product_listings`
     - `unauthenticated_read_product_inventory`
     - `unauthenticated_read_checkouts`
     - `unauthenticated_write_checkouts`
     - `unauthenticated_read_customers`
   - Install app and copy Storefront API access token

4. **Add credentials to .env:**
```env
SHOPIFY_STORE_DOMAIN=your-dev-store.myshopify.com
SHOPIFY_STOREFRONT_ACCESS_TOKEN=your_token_here
PUBLIC_SHOPIFY_STORE_DOMAIN=your-dev-store.myshopify.com
```

**Run the development server:**

```bash
# Start Astro dev server
npm run dev

# Or use yarn
yarn dev
```

**Access the storefront:**
- Local dev: `http://localhost:4321`
- Browse products, add to cart, test checkout flow

**Build for production:**

```bash
# Build optimized production bundle
npm run build

# Preview production build
npm run preview
```

**Environment Variables:**
Create a `.env` file in the project root:
```env
# Shopify Configuration
SHOPIFY_STORE_DOMAIN=your-store.myshopify.com
SHOPIFY_STOREFRONT_ACCESS_TOKEN=your_storefront_access_token
PUBLIC_SHOPIFY_STORE_DOMAIN=your-store.myshopify.com

# Optional: Analytics
PUBLIC_GA_TRACKING_ID=G-XXXXXXXXXX
```

---

## API Integration

**Shopify Storefront API (GraphQL):**

The storefront uses Shopify's Storefront API v2023-10 for all product and checkout operations.

**Example GraphQL queries used:**

```graphql
# Get all products
query GetProducts($first: Int!) {
  products(first: $first) {
    edges {
      node {
        id
        title
        handle
        description
        priceRange {
          minVariantPrice {
            amount
            currencyCode
          }
        }
        images(first: 5) {
          edges {
            node {
              url
              altText
            }
          }
        }
        variants(first: 10) {
          edges {
            node {
              id
              title
              priceV2 {
                amount
                currencyCode
              }
              availableForSale
            }
          }
        }
      }
    }
  }
}

# Create checkout
mutation checkoutCreate($input: CheckoutCreateInput!) {
  checkoutCreate(input: $input) {
    checkout {
      id
      webUrl
      lineItems(first: 10) {
        edges {
          node {
            title
            quantity
          }
        }
      }
    }
  }
}
```

**API Client Configuration:**

```typescript
// src/lib/shopify.ts
const SHOPIFY_API_URL = `https://${import.meta.env.SHOPIFY_STORE_DOMAIN}/api/2023-10/graphql.json`;
const STOREFRONT_TOKEN = import.meta.env.SHOPIFY_STOREFRONT_ACCESS_TOKEN;

async function shopifyFetch<T>(query: string, variables = {}) {
    const response = await fetch(SHOPIFY_API_URL, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'X-Shopify-Storefront-Access-Token': STOREFRONT_TOKEN
        },
        body: JSON.stringify({ query, variables })
    });
    return response.json() as Promise<T>;
}
```

**Rate Limits:**
- Storefront API: 1000 points per minute
- Each query costs points based on complexity
- Implement caching to reduce API calls

**Caching Strategy:**
- Static product pages cached at build time
- SSR pages cached on CDN edge (Vercel/Netlify)
- Client-side cache for cart and user data

---

## What's Next

**Planned improvements:**
- Add product reviews and ratings system
- Implement customer order history page
- Build advanced recommendation engine (related products, "bought together")
- Add product comparison feature
- Implement wishlist sharing functionality
- Add Google Analytics and conversion tracking
- Build admin dashboard for content management
- Add multiple language support (i18n)
- Implement progressive web app (PWA) features
- Add product quick view modal
- Build gift card support
- Add subscription product support
- Implement customer loyalty program
- Add live chat support integration

**Want to contribute?**
The codebase uses modern Astro patterns. Focus areas: Shopify API integration enhancements, UI/UX improvements, performance optimizations.

---

## Deployment

**Vercel (Recommended):**

```bash
# 1. Install Vercel CLI
npm install -g vercel

# 2. Login to Vercel
vercel login

# 3. Deploy
vercel deploy --prod

# 4. Set environment variables in Vercel dashboard
# Settings → Environment Variables → Add:
# SHOPIFY_STORE_DOMAIN
# SHOPIFY_STOREFRONT_ACCESS_TOKEN
# PUBLIC_SHOPIFY_STORE_DOMAIN
```

**Or connect GitHub repo:**
1. Push code to GitHub
2. Import project in Vercel dashboard
3. Add environment variables
4. Deploy automatically on push

**Netlify:**

```bash
# 1. Install Netlify CLI
npm install -g netlify-cli

# 2. Login and deploy
netlify login
netlify init
netlify deploy --prod

# 3. Set environment variables
netlify env:set SHOPIFY_STORE_DOMAIN "your-store.myshopify.com"
netlify env:set SHOPIFY_STOREFRONT_ACCESS_TOKEN "your_token"
```

**Or use `netlify.toml` (already configured):**
1. Connect repo to Netlify
2. Add environment variables in Netlify dashboard
3. Deploy automatically on git push

**Docker:**

```bash
# Build image
docker build -t shopplate-astro .

# Run container
docker run -p 4321:4321 \
  -e SHOPIFY_STORE_DOMAIN="your-store.myshopify.com" \
  -e SHOPIFY_STOREFRONT_ACCESS_TOKEN="your_token" \
  -e PUBLIC_SHOPIFY_STORE_DOMAIN="your-store.myshopify.com" \
  shopplate-astro

# Or use Docker Compose
docker-compose up -d
```

**Environment Variables for Production:**
- `SHOPIFY_STORE_DOMAIN` - Your Shopify store domain (e.g., store.myshopify.com)
- `SHOPIFY_STOREFRONT_ACCESS_TOKEN` - Storefront API access token
- `PUBLIC_SHOPIFY_STORE_DOMAIN` - Public-facing store domain
- `NODE_VERSION` - Node.js version (20.10+)

**Performance Optimization:**
- Astro SSR provides excellent Core Web Vitals
- Vercel Edge Network: <100ms response times globally
- Image optimization automatic with Astro Image
- Tailwind CSS purged for minimal CSS bundle
- React hydration only where needed (islands architecture)

---

## 📹 Demo Video

> **Recording Instructions:** A 3-5 minute walkthrough showcasing the key features of this project.

### What to Demonstrate

**Suggested Timeline:**
- **0:00-0:30** - Project overview and homepage features
- **0:30-2:00** - Product browsing, filtering, and search
- **2:00-3:30** - Cart management and checkout flow
- **3:30-5:00** - User authentication and additional features

### Features to Showcase

- **Homepage** - Hero banner, featured products, dynamic slider
- **Product Catalog** - Browse all products with filtering and sorting
- **Search & Filter** - Search products, filter by category/price/vendor
- **Product Details** - Image zoom, variant selection, add to cart
- **Shopping Cart** - View cart, update quantities, remove items
- **Checkout** - Seamless Shopify checkout integration
- **Authentication** - User registration and login
- **Dark Mode** - Toggle between light and dark themes
- **Responsive Design** - Mobile, tablet, and desktop views
- **Infinite Scroll** - Automatic product loading on scroll

### Recording Setup

**Prerequisites:**
```bash
# Install dependencies
npm install

# Create .env file with Shopify credentials
# (See Getting Started section for details)

# Start development server
npm run dev
```

**OBS Studio Settings:**
- Resolution: 1920x1080 (1080p)
- FPS: 30
- Format: MP4 (H.264)
- Audio: Include microphone narration (optional)

**Steps:**
1. Start the application: `npm run dev`
2. Open OBS Studio and set up screen capture
3. Navigate to `http://localhost:4321`
4. Record the demonstration following the timeline above
5. Save video as `demo.mp4` in the project root directory
6. (Optional) Upload to YouTube and update README with embed link

### Quick Demo Commands

```bash
# Navigate to project
cd "d:\Personal-Projects\Web projects\shopplate-astro"

# Start application
npm run dev

# Access the app
# Open browser to http://localhost:4321
```

**Video file:** Once recorded, save as `demo.mp4` in this directory.

---

## License

**Proprietary Software** - All rights reserved.

This software is provided for personal use and evaluation only. No license is granted for commercial use, modification, or distribution without explicit written permission from the author.

For licensing inquiries, please contact the repository owner.
