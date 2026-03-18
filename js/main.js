/* ================================================================
   TapLink — Main JavaScript
   ================================================================ */

// --------------- Scroll-triggered animations ---------------

const animateElements = document.querySelectorAll('[data-animate]');

const observerOptions = {
  root: null,
  rootMargin: '0px 0px -60px 0px',
  threshold: 0.1,
};

const observer = new IntersectionObserver((entries) => {
  entries.forEach((entry) => {
    if (entry.isIntersecting) {
      entry.target.classList.add('is-visible');
      observer.unobserve(entry.target);
    }
  });
}, observerOptions);

animateElements.forEach((el) => observer.observe(el));

// --------------- Navbar scroll effect ---------------

const nav = document.getElementById('nav');

function handleNavScroll() {
  if (window.scrollY > 40) {
    nav.classList.add('nav--scrolled');
  } else {
    nav.classList.remove('nav--scrolled');
  }
}

window.addEventListener('scroll', handleNavScroll, { passive: true });

// --------------- Mobile menu toggle ---------------

const navToggle = document.getElementById('nav-toggle');
const mobileMenu = document.getElementById('mobile-menu');

navToggle.addEventListener('click', () => {
  const isOpen = mobileMenu.classList.toggle('is-open');
  navToggle.setAttribute('aria-expanded', String(isOpen));
  document.body.style.overflow = isOpen ? 'hidden' : '';
});

mobileMenu.querySelectorAll('a').forEach((link) => {
  link.addEventListener('click', () => {
    mobileMenu.classList.remove('is-open');
    navToggle.setAttribute('aria-expanded', 'false');
    document.body.style.overflow = '';
  });
});

// --------------- Smooth scroll for anchor links ---------------

document.querySelectorAll('a[href^="#"]').forEach((anchor) => {
  anchor.addEventListener('click', (e) => {
    const target = document.querySelector(anchor.getAttribute('href'));
    if (target) {
      e.preventDefault();
      const navHeight = nav.offsetHeight;
      const targetPosition = target.getBoundingClientRect().top + window.scrollY - navHeight - 16;
      window.scrollTo({ top: targetPosition, behavior: 'smooth' });
    }
  });
});

// --------------- Parallax tilt on hero card ---------------

const cardScene = document.querySelector('.hero__card-scene');

if (cardScene && window.matchMedia('(min-width: 769px)').matches) {
  const heroSection = document.querySelector('.hero');

  heroSection.addEventListener('mousemove', (e) => {
    const rect = heroSection.getBoundingClientRect();
    const x = (e.clientX - rect.left) / rect.width - 0.5;
    const y = (e.clientY - rect.top) / rect.height - 0.5;

    const rotateX = y * -12;
    const rotateY = x * 12;

    cardScene.style.transform = `perspective(1000px) rotateX(${rotateX}deg) rotateY(${rotateY}deg)`;
  });

  heroSection.addEventListener('mouseleave', () => {
    cardScene.style.transform = 'perspective(1000px) rotateX(0) rotateY(0)';
    cardScene.style.transition = 'transform 0.6s cubic-bezier(0.16, 1, 0.3, 1)';
    setTimeout(() => {
      cardScene.style.transition = '';
    }, 600);
  });
}

// --------------- Counter animation for stats ---------------

function animateCounters() {
  const stats = document.querySelectorAll('.hero__stat strong');
  stats.forEach((stat) => {
    const text = stat.textContent;
    const match = text.match(/^([\d.]+)(.*)$/);
    if (!match) return;

    const targetNum = parseFloat(match[1]);
    const suffix = match[2];
    const duration = 1500;
    const startTime = performance.now();

    function update(currentTime) {
      const elapsed = currentTime - startTime;
      const progress = Math.min(elapsed / duration, 1);
      const eased = 1 - Math.pow(1 - progress, 3);
      const currentVal = targetNum * eased;

      if (Number.isInteger(targetNum)) {
        stat.textContent = Math.round(currentVal).toLocaleString() + suffix;
      } else {
        stat.textContent = currentVal.toFixed(1) + suffix;
      }

      if (progress < 1) {
        requestAnimationFrame(update);
      }
    }

    requestAnimationFrame(update);
  });
}

const heroStats = document.querySelector('.hero__stats');
if (heroStats) {
  const statsObserver = new IntersectionObserver(
    (entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          animateCounters();
          statsObserver.unobserve(entry.target);
        }
      });
    },
    { threshold: 0.5 }
  );
  statsObserver.observe(heroStats);
}
