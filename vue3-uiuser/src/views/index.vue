<template>
  <div class="home">
    <!-- Top Visual: City and Crowd -->
    <el-carousel height="520px" class="banner" indicator-position="outside" arrow="always">
      <el-carousel-item v-for="item in carouselList" :key="item.id">
        <div class="banner-image" :style="{ backgroundImage: `url(${item.image})` }"></div>
      </el-carousel-item>
    </el-carousel>

    <!-- Module One: Image Right, Text Left -->
    <div class="content-section section-soft">
      <el-row :gutter="40" type="flex" align="middle">
        <el-col :span="12">
          <div class="text-content">
            <h2>Trusted Clothing Exchange & Second-hand Trading</h2>
            <p>We integrate "circularity", "quality", and "trust" into one platform, providing end-to-end support from exchange and consignment to instant transactions. Multi-layer risk control and credit systems ensure every piece of clothing is safely and transparently circulated.</p>
            <p>Enterprise-level asset management concepts are introduced into the second-hand scene: standardized categories, batch tracking, quality inspection reports, traceability archives, and price reference indices, helping buyers and sellers quickly reach trusted transactions.</p>
            <p>Whether it's high-street fashion, business formal wear, or outdoor functional gear, we use refined operations and technology to protect every delivery.</p>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="image-content">
            <img :src="sectionImages[0]" alt="Trusted Second-hand Trading">
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- Module Two: Image Left, Text Right -->
    <div class="content-section section-accent">
      <el-row :gutter="40" type="flex" align="middle">
        <el-col :span="12">
          <div class="image-content">
            <img :src="sectionImages[1]" alt="Smart Matching">
          </div>
        </el-col>
        <el-col :span="12">
          <div class="text-content">
            <h2>Smart Matching · Efficient Matching</h2>
            <p>Based on tag profiles and preference models, intelligent distribution and matching: size, style, occasion, season, budget - find the perfect exchange/purchase target with one click.</p>
            <p>Consistent experience across multiple terminals, responsive layout + skeleton screen loading, ensuring both mobile and desktop have enterprise-level backend-like smoothness and stability.</p>
            <p>Real-time transaction process visibility: quality inspection, pricing, logistics, and delivery nodes are fully traceable, forming auditable operation logs.</p>
          </div>
        </el-col>
      </el-row>
    </div>

    <!-- Module Three: Image Right, Text Left -->
    <div class="content-section section-contrast">
      <el-row :gutter="40" type="flex" align="middle">
        <el-col :span="12">
          <div class="text-content">
            <h2>Operational-Level Service & After-Sales Guarantee</h2>
            <p>Self-operated + crowd-testing quality inspection dual channels, supporting multi-dimensional proof through photos/videos/reports; rapid arbitration of violations and defects to ensure platform credibility.</p>
            <p>Immersive content operations: styling guides, care knowledge, material science, and brand maintenance advice to extend the life cycle of clothing.</p>
            <p>Full-process risk control: device fingerprinting, behavior models, blacklist cross-validation; automatic alerts for abnormal prices, frequent cancellations, and false descriptions.</p>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="image-content">
            <img :src="sectionImages[2]" alt="Operations & Guarantee">
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
// 导入轮播图
import lunbo1 from '@/assets/images/index/lunbo1.jpg'
import lunbo2 from '@/assets/images/index/lunbo2.jpg'
import lunbo3 from '@/assets/images/index/lunbo3.jpg'

// 导入内容区域图片
import home1 from '@/assets/images/index/home1.jpg'
import home2 from '@/assets/images/index/home2.jpg'
import home3 from '@/assets/images/index/home3.jpg'

export default {
  name: 'Home',
  data() {
    return {
      carouselList: [
        {
          id: 1,
          image: lunbo1,
          title: 'Circular Fashion · Premium Quality',
          description: 'Curated Quality Inspection + Trusted Circulation, Giving Every Garment New Value'
        },
        {
          id: 2,
          image: lunbo2,
          title: 'Smart Matching · Precise Matching',
          description: 'Multi-dimensional Smart Matching by Size, Style, Budget to Improve Transaction Efficiency'
        },
        {
          id: 3,
          image: lunbo3,
          title: 'Full-Chain Visibility · Auditable',
          description: 'Quality Inspection, Pricing, Logistics, After-Sales Fully Tracked with Enterprise-Level Credibility'
        }
      ],
      sectionImages: [
        home1,
        home2,
        home3
      ]
    }
  },
  methods: {
  }
}
</script>

<style scoped>
.home {
  width: 100%;
  background: #f7f9fc;
  color: #1d2230;
  font-family: "STKaiti", "Source Han Serif SC", "Noto Serif SC", "Hiragino Sans GB", "Songti SC", serif;
}

.banner {
  margin-bottom: 56px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 20px 60px rgba(25, 39, 52, 0.16);
}

.banner-image {
  width: 100%;
  height: 520px;
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  filter: saturate(1.02) contrast(1.02);
}

.banner-content {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  text-align: center;
  color: #1d2230;
  text-shadow: 0 10px 32px rgba(0, 0, 0, 0.18);
  letter-spacing: 0.4px;
  padding: 0 24px;
}

.banner-content h2 {
  font-size: 38px;
  margin-bottom: 16px;
  font-weight: 700;
}

.banner-content p {
  font-size: 18px;
  opacity: 0.9;
}

.content-section {
  padding: 80px 120px;
  border-top: 1px solid rgba(0, 0, 0, 0.04);
  border-bottom: 1px solid rgba(0, 0, 0, 0.03);
}

.section-soft {
  background: linear-gradient(120deg, #ffffff 0%, #f7fbff 50%, #fdf7ff 100%);
}

.section-accent {
  background: linear-gradient(135deg, #eef4ff 0%, #f4f9ff 40%, #f7fff6 100%);
}

.section-contrast {
  background: linear-gradient(135deg, #ffffff 0%, #f4f7fb 35%, #eef2fb 70%, #fef7f0 100%);
}

.text-content {
  padding: 12px 12px 12px 0;
}

.text-content h2 {
  font-size: 30px;
  color: #1d2230;
  margin-bottom: 22px;
  font-weight: 700;
  letter-spacing: 0.8px;
  line-height: 1.35;
}

.text-content p {
  font-size: 16px;
  line-height: 1.85;
  color: #4b5567;
  margin-bottom: 16px;
  letter-spacing: 0.2px;
}

.image-content {
  width: 100%;
  height: 420px;
  overflow: hidden;
  border-radius: 12px;
  box-shadow: 0 18px 48px rgba(31, 63, 104, 0.12);
  border: 1px solid rgba(0, 0, 0, 0.04);
}

.image-content img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.45s ease, filter 0.45s ease;
  filter: saturate(1.05) contrast(1.03);
}

.image-content img:hover {
  transform: scale(1.04);
  filter: saturate(1.15) contrast(1.05);
}

@media screen and (max-width: 1200px) {
  .content-section {
    padding: 60px 64px;
  }
  .image-content {
    height: 360px;
  }
}

@media screen and (max-width: 768px) {
  .content-section {
    padding: 40px 24px;
  }
  .image-content {
    height: 280px;
    margin: 20px 0;
  }
  .banner-content h2 {
    font-size: 28px;
  }
  .banner-content p {
    font-size: 15px;
  }
}
</style>
