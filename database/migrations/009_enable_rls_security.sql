-- =============================================================================
-- Migration: 009_enable_rls_security.sql
-- Purpose: Enable Row Level Security on all tables for Supabase security
-- Date: 2026-02-04
-- Status: Already applied via Supabase MCP
-- =============================================================================

-- Enable RLS on all tables
ALTER TABLE public.users ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.farms ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.farm_types ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.animal_types ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.diseases ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.disease_reports ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.farm_animals ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.animals ENABLE ROW LEVEL SECURITY;
ALTER TABLE public.disease_animal_types ENABLE ROW LEVEL SECURITY;

-- Permissive policies for Spring Boot backend (connects as postgres user)
CREATE POLICY "backend_full_access" ON public.users FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "backend_full_access" ON public.farms FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "backend_full_access" ON public.farm_types FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "backend_full_access" ON public.animal_types FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "backend_full_access" ON public.diseases FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "backend_full_access" ON public.disease_reports FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "backend_full_access" ON public.farm_animals FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "backend_full_access" ON public.animals FOR ALL USING (true) WITH CHECK (true);
CREATE POLICY "backend_full_access" ON public.disease_animal_types FOR ALL USING (true) WITH CHECK (true);
