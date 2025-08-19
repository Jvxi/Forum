export default {
  mounted(el, binding) {
    const options = typeof binding?.value === 'object' ? binding.value : {};
    const rootMargin = options.rootMargin || '0px';
    const threshold = options.threshold ?? 0.1;
    const once = options.once !== false; // default true

    el.classList.add('reveal');

    const observer = new IntersectionObserver((entries, obs) => {
      entries.forEach(entry => {
        if (entry.isIntersecting) {
          el.classList.add('reveal-visible');
          if (once) obs.unobserve(el);
        } else if (!once) {
          el.classList.remove('reveal-visible');
        }
      });
    }, { rootMargin, threshold });

    observer.observe(el);
    el._revealObserver = observer;
  },
  unmounted(el) {
    if (el._revealObserver) {
      el._revealObserver.disconnect();
      delete el._revealObserver;
    }
  }
}


